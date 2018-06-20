package entity.changexchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import for currency tracker
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.ExchangeRateTracker;
import entity.changexchange.utils.NotificationService;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.TokenNotification;
import entity.changexchange.utils.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.postgresql.core.Oid.JSON;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Location related fields.
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private Location lastLocation;
    private GoogleApiClient googleApiClient;

    // Menu related fields.
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private String title;

    private User user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (User) getIntent().getSerializableExtra("user");

        // Menu setup.
        setupMenu();

        // Authorise and setup location.
        googleApiClient = new GoogleApiClient.Builder(
                this,
                this,
                this
        ).addApi(LocationServices.API).build();
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_ACCESS_COARSE_LOCATION
        );

        // Adapter to trigger offer reload on spinner selection.
        AdapterView.OnItemSelectedListener reloader = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDisplay(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        // Create adapter for selection of currencies and link to dropdown objects.
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        );
        Spinner selling = findViewById(R.id.offers_selling);
        selling.setAdapter(adapter);
        selling.setSelection(user.getCurrency().ordinal());

        Spinner buying = findViewById(R.id.offers_buying);
        buying.setAdapter(adapter);
        buying.setSelection(user.getCurrency().ordinal());

        // Create adapter for selection of airport location.
        ArrayAdapter<Airport> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Airport.values()
        );
        Spinner at = findViewById(R.id.offers_at);
        at.setAdapter(adapter1);


        // If coming from MakeAnOffer, reset Spinner to previous values
        String sellingPrev = getIntent().getStringExtra("selling");
        if (sellingPrev != null) {
            selling.setSelection(Currency.valueOf(sellingPrev).ordinal());
        }
        String buyingPrev = getIntent().getStringExtra("buying");
        if (buyingPrev != null) {
            buying.setSelection(Currency.valueOf(buyingPrev).ordinal());
        }
        String atPrev = getIntent().getStringExtra("at");
        if (atPrev != null) {
            at.setSelection(Airport.valueOf(atPrev).ordinal());
        }

        // Check if user has given location permission and set default Airport.
        if (locationPermitted()) {
            this.<Spinner>findViewById(R.id.offers_at).setSelection(findNearestAirport().ordinal());
        }

        // Fetch exchange rate for selected currencies correct exchange rate and offers
        updateDisplay();

        // Setting adequat on clicks
        selling.setOnItemSelectedListener(reloader);
        buying.setOnItemSelectedListener(reloader);
        at.setOnItemSelectedListener(reloader);
        // When adding amount, refreshes offers.
        this.<EditText>findViewById(R.id.offers_max_amt).setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                updateDisplay();
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        // Clicking swap button, swaps the content of the two spinners (currency from / to)
        this.<ImageButton>findViewById(R.id.offers_swap_curr).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Spinner from = findViewById(R.id.offers_selling);
                        Spinner to = findViewById(R.id.offers_buying);
                        int fromVal = from.getSelectedItemPosition();

                        from.setSelection(to.getSelectedItemPosition());
                        to.setSelection(fromVal);

                        updateDisplay(v);
                    }
                }
        );

        final SwipeRefreshLayout layout = findViewById(R.id.offers_swiper);

        // Pulling offers down will refresh them.
        layout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateDisplay();
                        layout.setRefreshing(!layout.isRefreshing());
                    }
                }
        );

        // Clicking on (+) brings up offer creation activity with smart default fields.
        this.<FloatingActionButton>findViewById(R.id.button_new_offer).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(
                                new Intent(MainActivity.this, MakeAnOffer.class)
                                        .putExtra("buying", getCurSelling())
                                        .putExtra("selling", getCurBuying())
                                        .putExtra("airport", getLocation())
                                        .putExtra("amount", getAmount())
                                        .putExtra("user", user)
                        );
                    }
                });
    }


    /**
     * UTIL RELATED METHODS
     */

    /**
     * Clicking on an offer brings up contact details for it.
     */
    public void selectOffer(View view) {
        startActivity(new Intent(MainActivity.this, ContactDetails.class)
                        .putExtra("buying", getCurSelling())
                        .putExtra("selling", getCurBuying())
                        .putExtra("airport", getLocation())
                        .putExtra("nickname",
                        ((TextView) view.findViewById(R.id.offer_poster_hidden)).getText().toString())
                        .putExtra("user", user)
        );
    }

    /**
     * Wrappers for exchange rate fetching and database requesting
     */
    private void updateDisplay() {
        // Fetch exchange rate
        new ExchangeRateTracker(this.<TextView>findViewById(R.id.offer_exchange_rate))
                .execute(getCurSelling(), getCurBuying());
        // Show offers
        new RequestDatabase(this, user).execute(
                "SELECT * FROM offers WHERE buying='"
                        + getCurSelling() + "' and selling='"
                        + getCurBuying() + "' and location='" + getLocation() + "' "
                        + "ORDER BY ABS(amount-" + getAmount() + ");"
        );
    }

    public void updateDisplay(View view) {
        // For spinners
        updateDisplay();
    }

    private String getCurSelling() {
        return ((Spinner) findViewById(R.id.offers_selling)).getSelectedItem().toString();
    }

    private String getCurBuying() {
        return ((Spinner) findViewById(R.id.offers_buying)).getSelectedItem().toString();
    }

    private String getLocation() {
        return ((Spinner) findViewById(R.id.offers_at)).getSelectedItem().toString();
    }

    private float getAmount() {
        String amount = ((EditText) findViewById(R.id.offers_max_amt)).getText().toString();
        return amount.isEmpty() ? 0.0f : Float.parseFloat(amount);
    }

    /**
     * LOCATION RELATED METHODS
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    // Not granted, i.e. warn user that no location will be used.
                    Toast.makeText(
                            this,
                            "Unable to access location.\nDefault Airport might not be optimal.",
                            Toast.LENGTH_SHORT
                    ).show();
                break;
        }
    }

    /**
     * Uses user location to find the nearest known airport. TODO: Make this more efficient.
     */
    private Airport findNearestAirport() {
        if (lastLocation == null)
            return Airport.DEFAULT;
        Airport closest = Airport.DEFAULT;
        for (Airport airport : Airport.values()) {
            if (lastLocation.distanceTo(airport.getLocation())
                    < lastLocation.distanceTo(closest.getLocation()))
                closest = airport;
        }
        return closest;
    }

    /**
     * Check that a user has given permission to use location services.
     */
    private boolean locationPermitted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @SuppressLint("MissingPermission") // Permission is checked.
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (locationPermitted())
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * MENU RELATED METHODS
     */

    private void setupMenu() {
        drawerList = findViewById(R.id.navList);
        drawerLayout = findViewById(R.id.drawer_layout);
        title = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addDrawerItems() {
        String[] tabs = {"Offers", "Profile", "My Offers", "Messages", "Settings"};
        drawerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, tabs
        );
        drawerList.setAdapter(drawerAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Do nothing.
                        Toast.makeText(MainActivity.this, "Already in Offers!", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, Profile.class)
                                .putExtra("user", user));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, MyOffers.class)
                                .putExtra("user", user));
                        break;
                    case 3:
//                        startActivity(new Intent(MainActivity.this, Messages.class)
//                                .putExtra("user", user));
                        Toast.makeText(MainActivity.this, "Messages coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
//                        startActivity(new Intent(MainActivity.this, Settings.class));
                        Toast.makeText(MainActivity.this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.test, R.string.test) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("ChangeXchange");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
