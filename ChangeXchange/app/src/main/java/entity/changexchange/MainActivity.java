package entity.changexchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import for currency tracker
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.Offer;
import entity.changexchange.utils.ExchangeRateTracker;
import entity.changexchange.utils.RequestDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private Location lastLocation;
    private GoogleApiClient googleApiClient;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleApiClient = new GoogleApiClient.Builder(
                this,
                this,
                this
        ).addApi(LocationServices.API).build();

        // Create adapter for selection of currencies and link to dropdown objects.
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        );
        Spinner from = findViewById(R.id.offers_from);
        from.setAdapter(adapter);
        from.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateDisplay(v);
                return false;
            }
        });
        Spinner to = findViewById(R.id.offers_to);
        to.setAdapter(adapter);
        to.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateDisplay(v);
                return false;
            }
        });

        // Create adapter for selection of airport location.
        ArrayAdapter<Airport> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Airport.values()
        );
        this.<Spinner>findViewById(R.id.offers_at).setAdapter(adapter1);

        // Check if user has given location permission and set default Airport.
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                PERMISSION_ACCESS_COARSE_LOCATION
        );
        if (locationPermitted())
            this.<Spinner>findViewById(R.id.offers_at).setSelection(findNearestAirport().ordinal());

        // Fetch exchange rate for selected currencies correct exchange rate and offers
        updateDisplay();

//        offers.add(new Offer("John", Currency.USD, Currency.EUR, 15, Airport.LGW));
//        offers.add(new Offer("Smith", Currency.CHF, Currency.JPY, (float) 9.15, Airport.LHR));
//        offers.add(new Offer("Lea", Currency.JPY, Currency.EUR, (float) 0.1231, Airport.STD));
//        offers.add(new Offer("Bla", Currency.CAD, Currency.AUD, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.AUD, Currency.CHF, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.EUR, Currency.AUD, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.JPY, Currency.EUR, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.CHF, Currency.AUD, 151241, Airport.LTN));


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

        // Pulling offers down will refresh them.
        this.<SwipeRefreshLayout>findViewById(R.id.offers_swiper).setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateDisplay();
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
     * Clicking on an offer brings up contact details for it.
     */
    public void selectOffer(View view) {
        startActivity(new Intent(MainActivity.this, sendText.class)
                    .putExtra("CONTACT", R.id.offer_title));
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
                        + getCurFrom() + "' and selling='" + getCurTo() + "' "
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
     * Uses user location to find the nearest known airport. TODO: Make this more efficient.
     */
    private Airport findNearestAirport() {
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
}
