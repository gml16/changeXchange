package entity.changexchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

//import for currency tracker
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import entity.changexchange.utils.Currency;
import entity.changexchange.utils.ExchangeRateTracker;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        // Clicking on (+) brings up offer creation activity.
        this.<FloatingActionButton>findViewById(R.id.button_new_offer).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, MakeAnOffer.class));
                    }
                });


        // Create adapter for selection of currencies and link to dropdown objects.
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        );
        this.<Spinner>findViewById(R.id.offers_from).setAdapter(adapter);
        this.<Spinner>findViewById(R.id.offers_to).setAdapter(adapter);

        // Fetch exchange rate for selected currencies correct exchange rate
        fetchExchangeRate();

        // Swaps content of the two spinners (currency from / to)
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

//        Test for the layout manager
//        // Setup container for offers.
//        RecyclerView layout = findViewById(R.id.offer_container);
//        // Content of offers won't change the container size.
//        layout.setHasFixedSize(true);
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        layout.setLayoutManager(manager);
//        RecyclerView.Adapter adapter = new OfferAdapter();
//        layout.setAdapter(adapter);
    }

    /**
     * Looks up the exchange rate of the selected currency values.
     */
    private void fetchExchangeRate()
    {
        this.<TextView>findViewById(R.id.offer_exchange_rate).setText(String.valueOf(Math.random()));
        // TODO: Fix exchange rate fetcher and add this.
//        this.<TextView>findViewById(R.id.offer_exchange_rate).setText(
//                String.valueOf(
//                        ExchangeRateTracker.getExchangeRate(
//                                ((Spinner) findViewById(R.id.offers_from))
//                                        .getSelectedItem().toString(),
//                                ((Spinner) findViewById(R.id.offers_to))
//                                        .getSelectedItem().toString()
//                        )
//                )
//        );
    }


}
