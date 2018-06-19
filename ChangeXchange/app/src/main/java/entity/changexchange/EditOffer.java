package entity.changexchange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.Offer;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

import static entity.changexchange.utils.Util.NEG_THRESHOLD;
import static entity.changexchange.utils.Util.filter;

public class EditOffer extends AppCompatActivity {

    private Offer offer;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer);

        offer = (Offer) getIntent().getSerializableExtra("offer");
        user = (User) getIntent().getSerializableExtra("user");

        // Setup currencies to current values of the offer.
        ArrayAdapter<Currency> currencyAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        );
        Spinner selling = findViewById(R.id.edit_offer_currency_from);
        Spinner buying = findViewById(R.id.edit_offer_currency_to);
        selling.setAdapter(currencyAdapter);
        buying.setAdapter(currencyAdapter);
        selling.setSelection(offer.getSelling().ordinal());
        buying.setSelection(offer.getBuying().ordinal());

        // Setup amount to current values of the offer.
        this.<TextView>findViewById(R.id.edit_offer_amount).setText(String.valueOf(offer.getAmount()));

        // Setup location to current values of the offer.
        ArrayAdapter<Airport> airportAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Airport.values()
        );
        Spinner location = findViewById(R.id.edit_offer_location);
        location.setAdapter(airportAdapter);
        location.setSelection(offer.getLocation().ordinal());

        // Setup note to current values of the offer.
        this.<TextView>findViewById(R.id.edit_offer_note).setText(String.valueOf(offer.getNote()));

        // Clicking on confirm does some checks them submits the offer.
        this.<Button>findViewById(R.id.edit_offer_submit).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitChanges();
                    }
                }
        );
    }

    private void submitChanges() {

        // Get entered values.
        String selling = ((Spinner) findViewById(R.id.edit_offer_currency_from))
                .getSelectedItem().toString();
        String buying = ((Spinner) findViewById(R.id.edit_offer_currency_to))
                .getSelectedItem().toString();
        String amount = ((EditText) findViewById(R.id.edit_offer_amount)).getText().toString();
        String location = ((Spinner) findViewById(R.id.edit_offer_location))
                .getSelectedItem().toString();
        String note = filter(((EditText) findViewById(R.id.edit_offer_note))
                .getText().toString());

        // If note hasn't been filled, replace with default value.
        if (note.isEmpty()) note = offer.getPoster_nickname() + " did not add a note.";

        //Erroneous amount entered. Deny clicking effect.
        if (amount.isEmpty() || Float.parseFloat(amount) <= NEG_THRESHOLD) {
            Toast.makeText(EditOffer.this, "Error: Invalid amount.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (selling.equals(buying)) {
            Toast.makeText(EditOffer.this, "Error: Invalid currencies.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the offer inside the database.
        new RequestDatabase().execute(
                "UPDATE offers SET "
                        + "buying='" + buying + "', "
                        + "selling='" + selling + "', "
                        + "amount='" + amount + "', "
                        + "location='" + location + "', "
                        + "note='" + note + "' WHERE "
                        + "nickname='" + offer.getPoster_nickname() + "' and "
                        + "buying='" + offer.getBuying() + "' and "
                        + "selling='" + offer.getSelling() + "' and "
                        + "amount='" + offer.getAmount() + "' and "
                        + "location='" + offer.getLocation() + "' and "
                        + "note='" + offer.getNote() + "';"
        );

        // Return to MyOffers.
        startActivity(new Intent(EditOffer.this, MyOffers.class)
                .putExtra("user", user));
    }
}
