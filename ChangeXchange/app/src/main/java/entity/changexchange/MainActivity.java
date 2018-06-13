package entity.changexchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.ExchangeRateTracker;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

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

        user = new User("Valerie", "Val92", Currency.CHF, "07460373769", 5.0);

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
        Spinner from = findViewById(R.id.offers_from);
        from.setAdapter(adapter);
        from.setOnItemSelectedListener(reloader);

        Spinner to = findViewById(R.id.offers_to);
        to.setAdapter(adapter);
        to.setOnItemSelectedListener(reloader);

        // Create adapter for selection of airport location.
        ArrayAdapter<Airport> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Airport.values()
        );
        Spinner at = findViewById(R.id.offers_at);
        at.setAdapter(adapter1);
        at.setOnItemSelectedListener(reloader);

        // If coming from MakeAnOffer, reset Spinner to previous values
        String fromPrev = getIntent().getStringExtra("from");
        if (fromPrev != null) {
            from.setSelection(Currency.valueOf(fromPrev).ordinal());
        }
        String toPrev = getIntent().getStringExtra("to");
        if (toPrev != null) {
            to.setSelection(Currency.valueOf(toPrev).ordinal());
        }
        String atPrev = getIntent().getStringExtra("at");
        if (atPrev != null) {
            at.setSelection(Airport.valueOf(atPrev).ordinal());
        }


        // Check if user has given location permission and set default Airport.
        if (locationPermitted())
            this.<Spinner>findViewById(R.id.offers_at).setSelection(findNearestAirport().ordinal());

        // Fetch exchange rate for selected currencies correct exchange rate and offers
        updateDisplay();

        // Clicking swap button, swaps the content of the two spinners (currency from / to)
        this.<ImageButton>findViewById(R.id.offers_swap_curr).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Spinner from = findViewById(R.id.offers_from);
                        Spinner to = findViewById(R.id.offers_to);
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
                        Intent intent = new Intent(MainActivity.this, MakeAnOffer.class);
                        intent.putExtra("buying", getCurTo());
                        intent.putExtra("selling", getCurFrom());
                        intent.putExtra("airport", getLocation());
                        intent.putExtra("amount", getAmount());
                        startActivity(intent);
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
        startActivity(new Intent(MainActivity.this, sendText.class)
                .putExtra("contact", R.id.offer_title));
    }

    /**
     * Wrappers for exchange rate fetching and database requesting
     */
    private void updateDisplay() {
        // Fetch exchange rate
        new ExchangeRateTracker(this.<TextView>findViewById(R.id.offer_exchange_rate))
                .execute(getCurFrom(), getCurTo());
        // Show offers
        new RequestDatabase(this).execute(
                "SELECT * FROM offers WHERE buying='"
                        + getCurFrom() + "' and selling='"
                        + getCurTo() + "' and location='" + getLocation() + "' "
                        + "ORDER BY ABS(amount-" + getAmount() + ");"
        );
    }

    public void updateDisplay(View view) {
        // For spinners
        updateDisplay();
    }

    private String getCurFrom() {
        return ((Spinner) findViewById(R.id.offers_from)).getSelectedItem().toString();
    }

    private String getCurTo() {
        return ((Spinner) findViewById(R.id.offers_to)).getSelectedItem().toString();
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
        String[] tabs = {"Offers", "Profile", "My Offers" , "Messages", "Settings"};
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
