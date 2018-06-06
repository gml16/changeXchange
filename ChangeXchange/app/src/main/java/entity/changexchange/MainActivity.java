package entity.changexchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

//import for currency tracker
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.Offer;
import entity.changexchange.utils.ExchangeRateTracker;
import entity.changexchange.utils.RequestDatabase;

public class MainActivity extends AppCompatActivity {

    private final List<Offer> offers = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Fetch exchange rate for selected currencies correct exchange rate
        fetchExchangeRate();

        // Get offers from database
        showOffers();

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
                        showOffers();
                    }
                }
        );

        // Clicking on (+) brings up offer creation activity.
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
     * Fetches offers with selected currencies from database.
     */
    private void showOffers() {
        new RequestDatabase(this).execute(
                "SELECT * FROM offers WHERE buying='"
                        + getCurFrom() + "' and selling='" + getCurTo() + "' "
                        + "ORDER BY ABS(amount-" + getAmount() + ");"
        );
    }

    /**
     * Looks up the exchange rate of the selected currency values.
     */
    private void fetchExchangeRate()
    {
        new ExchangeRateTracker(this.<TextView>findViewById(R.id.offer_exchange_rate))
                .execute(getCurFrom(), getCurTo()
        );
    }
    public void updateDisplay(View view) {
        // For spinners
        fetchExchangeRate();
        showOffers();
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
}
