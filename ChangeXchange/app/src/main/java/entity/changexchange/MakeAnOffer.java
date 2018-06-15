package entity.changexchange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;


public class MakeAnOffer extends AppCompatActivity {

    private static final float NEG_THRESHOLD = (float) 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_an_offer);
        final Intent intent = getIntent();

        final User user = (User) intent.getSerializableExtra("user");

        // Create adapter for list of currencies and link to dropdown objects.
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        );
        Spinner selling = findViewById(R.id.new_offer_currency_from);
        Spinner buying = findViewById(R.id.new_offer_currency_to);
        selling.setAdapter(adapter);
        buying.setAdapter(adapter);


        // Create adapter for list of airports.
        ArrayAdapter<Airport> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Airport.values()
        );
        Spinner airport = findViewById(R.id.new_offer_location);
        airport.setAdapter(adapter1);


        // Set selection to what was being searched on the offers page.
        selling.setSelection(Currency.valueOf(
                intent.getStringExtra("selling")).ordinal()
        );
        buying.setSelection(Currency.valueOf(
                intent.getStringExtra("buying")).ordinal()
        );
        airport.setSelection(Airport.valueOf(
                intent.getStringExtra("airport")).ordinal()
        );
        this.<EditText>findViewById(R.id.new_offer_amount).setText(
                String.valueOf(
                        intent.getFloatExtra("amount", 1.0f)
                )
        );

        // Submitting an offer triggers the migration of all the data to the database.
        this.<Button>findViewById(R.id.new_offer_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = ((EditText) findViewById(R.id.new_offer_amount)).getText().toString();

                //Erroneous amount entered. Deny clicking effect.
                if (amount.isEmpty() || Float.parseFloat(amount) <= NEG_THRESHOLD) return;

                String name = user.getNickname();

                String from = ((Spinner) findViewById(R.id.new_offer_currency_from))
                        .getSelectedItem().toString();
                String to = ((Spinner) findViewById(R.id.new_offer_currency_to))
                        .getSelectedItem().toString();
                String location = ((Spinner) findViewById(R.id.new_offer_location))
                        .getSelectedItem().toString();
                String note = ((EditText) findViewById(R.id.new_offer_note))
                        .getText().toString();

                // If note hasn't been filled, replace with default value.
                if (note.isEmpty()) note = name + " did not add a note.";

                // Erroneous amount entered. Deny clicking effect.
                if (from.isEmpty() || to.isEmpty() || location.isEmpty()) return;

                // TODO: Add note to database.

                new RequestDatabase().execute(
                        "INSERT INTO offers VALUES (" + name + ", '"
                                + from + "', '"
                                + to + "', '"
                                + amount + "', '"
                                + location + "', '"
                                + note + "');"
                );

                startActivity(new Intent(MakeAnOffer.this, MainActivity.class)
                        .putExtra("from", from)
                        .putExtra("to", to)
                        .putExtra("at", location)
                        .putExtra("user", user)
                );
            }
        });
    }
}
