package entity.changexchange;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.regex.Pattern;

import entity.changexchange.utils.Currency;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

public class EditProfile extends AppCompatActivity {

    // Illegal characters for nicknames.
    private static final char[] ILLEGALS = {'\r', '\t', '\n'};

    // Pattern for verifying syntax email.
    private static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        this.<Spinner>findViewById(R.id.edit_currency).setAdapter(
                new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, Currency.values()
        ));

        user = (User) getIntent().getSerializableExtra("user");

        this.<Button>findViewById(R.id.edit_profile_confirm).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newNickname = filter(
                                ((EditText) findViewById(R.id.edit_nickname)).getText().toString()
                        );
                        String newContact = filter(
                                ((EditText) findViewById(R.id.edit_contact)).getText().toString()
                        );
                        Currency newCurrency = Currency.valueOf(
                                ((Spinner) findViewById(R.id.edit_currency)).getSelectedItem().toString()
                        );

                        if (isInvalid(newContact)) {
                            Toast.makeText(
                                    EditProfile.this,
                                    "Please enter a valid contact method.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        updateUser(newNickname, newContact, newCurrency);

                        // Update the database.
                        new RequestDatabase().execute(
                                "UPDATE users SET nickname=" + newNickname
                                        + " contact=" + newContact
                                        + " currency=" + newCurrency
                                        + " WHERE nickname=" + user.getNickname()
                        );

                        // Pass de modified object back to profile.
                        startActivity(
                                new Intent(EditProfile.this, Profile.class)
                                        .putExtra("user", user)
                        );
                    }
                }
        );
    }

    /**
     * Checks input data for null and sets to already existing values if it is the case.
     */
    private void updateUser(String newNickname, String newContact, Currency newCurrency) {
        user.changeNickname(
                newNickname.isEmpty() ? user.getNickname() : newNickname
        );
        user.changeContact(
                newContact.isEmpty() ? user.getContact() : newContact
        );
        user.changeCurrency(
                newCurrency == null ? user.getCurrency() : newCurrency
        );
    }

    /**
     * Checks that the given contact is a valid email address / phone number / or AppMessaging.
     */
    private boolean isInvalid(String contact) {
        return contact.equals("In app")
                || !rfc2822.matcher(contact).matches();
                // TODO: Check for valid phone number.
    }

    /**
     * Removes illegal characters from user input before parsing.
     */
    private String filter(String userInput) {
        for (char c : ILLEGALS) {
            userInput = userInput.replace(c, ' ');
        }
        return userInput;
    }
}
