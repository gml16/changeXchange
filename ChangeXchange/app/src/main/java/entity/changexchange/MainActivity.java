package entity.changexchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

//import for currency tracker
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.Offer;
import entity.changexchange.utils.OfferAdapter;
import entity.changexchange.utils.ExchangeRateTracker;
import entity.changexchange.utils.RequestDatabase;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create adapter for selection of currencies and link to dropdown objects.
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        );
        this.<Spinner>findViewById(R.id.offers_from).setAdapter(adapter);
        this.<Spinner>findViewById(R.id.offers_to).setAdapter(adapter);

        // Fetch exchange rate for selected currencies correct exchange rate
        fetchExchangeRate();

        // Setup container for offers.
        List<Offer> offers = new ArrayList<>();
        RecyclerView offer_container = findViewById(R.id.offer_container);
        offer_container.setHasFixedSize(true);
        offer_container.setLayoutManager(new LinearLayoutManager(this));

        String fromString = ((Spinner) findViewById(R.id.offers_from))
                .getSelectedItem().toString();
        String toString =((Spinner) findViewById(R.id.offers_to))
                        .getSelectedItem().toString();
        new RequestDatabase(offers).execute("SELECT * FROM offers WHERE buying='" + fromString + "' and selling='" + toString + "';");
        Log.d("guy", "SELECT * FROM offers WHERE buying='" + fromString + "' and selling='" + toString + "';");

//        offers.add(new Offer("John", Currency.USD, Currency.EUR, 15, Airport.LGW));
//        offers.add(new Offer("Smith", Currency.CHF, Currency.JPY, (float) 9.15, Airport.LHR));
//        offers.add(new Offer("Lea", Currency.JPY, Currency.EUR, (float) 0.1231, Airport.STD));
//        offers.add(new Offer("Bla", Currency.CAD, Currency.AUD, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.AUD, Currency.CHF, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.EUR, Currency.AUD, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.JPY, Currency.EUR, 151241, Airport.LTN));
//        offers.add(new Offer("Bla", Currency.CHF, Currency.AUD, 151241, Airport.LTN));

        offer_container.setAdapter(new OfferAdapter(this, offers));

        // Switch to Messages / Profile activity.
        this.<BottomNavigationView>findViewById(R.id.navigation).setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_profile:
                                startActivity(new Intent(MainActivity.this, Profile.class));
                                break;
                            case R.id.navigation_offers:
                                //Do nothing.
                                break;
                            case R.id.navigation_messages:
                                //TODO:startActivity(new Intent(MainActivity.this, Messages.class));
                                break;
                        }
                        return false;
                    }
                });


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

                        fetchExchangeRate();
                    }
                }
        );

        // Clicking (UPDATE) reloads the exchange rate shown.
        this.<Button>findViewById(R.id.offers_update_exchange).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchExchangeRate();
                    }
                }
        );

        // Clicking on (+) brings up offer creation activity.
        this.<FloatingActionButton>findViewById(R.id.button_new_offer).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, MakeAnOffer.class));
                    }
                });
    }

    /**
     * Looks up the exchange rate of the selected currency values.
     */
    private void fetchExchangeRate()
    {
        new ExchangeRateTracker(this.<TextView>findViewById(R.id.offer_exchange_rate)).execute(
                ((Spinner) findViewById(R.id.offers_from))
                                        .getSelectedItem().toString(),
                ((Spinner) findViewById(R.id.offers_to))
                                        .getSelectedItem().toString()
        );
    }


}
