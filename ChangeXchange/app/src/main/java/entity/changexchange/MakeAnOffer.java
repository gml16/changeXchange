package entity.changexchange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import entity.changexchange.utils.Currency;


public class MakeAnOffer extends AppCompatActivity {

    private static final float NEG_THRESHOLD = (float) 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_an_offer);

        // Create adapter for list of currencies and link to dropdown objects.
        ArrayAdapter<Currency> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        );
        this.<Spinner>findViewById(R.id.new_offer_currency_from).setAdapter(adapter);
        this.<Spinner>findViewById(R.id.new_offer_currency_to).setAdapter(adapter);

        // Submitting an offer triggers the migration of all the data to the database.
        this.<Button>findViewById(R.id.new_offer_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = findViewById(R.id.new_offer_price).toString();

                if (amount.isEmpty() || Float.parseFloat(amount) <= NEG_THRESHOLD) {
                    //Erroneous amount entered. Deny clicking effect.
                    return;
                }

                String from = ((Spinner) findViewById(R.id.new_offer_currency_from))
                        .getSelectedItem().toString();
                String to = ((Spinner) findViewById(R.id.new_offer_currency_to))
                        .getSelectedItem().toString();

                if (from.isEmpty() || to.isEmpty()) {
                    //Erroneous amount entered. Deny clicking effect.
                    return;
                }

                // TODO: Get information from logged in user.

                // TODO: Post offer information to database.

                startActivity(new Intent(MakeAnOffer.this, MainActivity.class));
            }
        });
    }
}
