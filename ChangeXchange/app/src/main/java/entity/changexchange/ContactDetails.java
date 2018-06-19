package entity.changexchange;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

import static entity.changexchange.utils.Util.CONTACT;

public class ContactDetails extends AppCompatActivity {

    boolean requested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        final boolean fromInterests = getIntent().getStringExtra("interest") != null;

        // set the requested boolean correctly.
        requested = getIntent().getBooleanExtra("requested", false);

        if (fromInterests) {
            // Fetch the poster's preferred contact detail of the poster.
            new RequestDatabase(this.<TextView>findViewById(R.id.selected_contact), CONTACT)
                    .execute(
                            "SELECT * FROM users WHERE nickname='"
                                    + getIntent().getStringExtra("nickname") + "';"
                    );
            this.<Button>findViewById(R.id.selected_interest).setVisibility(View.GONE);
        } else {
            // Clicking on (INTERESTED) registers the users' interest
            this.<Button>findViewById(R.id.selected_interest).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!requested) {
                                new RequestDatabase().execute(
                                        "UPDATE offers SET interested_users=CONCAT(interested_users,',"
                                                + ((User) getIntent().getSerializableExtra("user")).getNickname() + "') "
                                                + "WHERE nickname='" + getIntent().getStringExtra("nickname") + "' and "
                                                + "buying='" + getIntent().getStringExtra("buying") + "' and "
                                                + "selling='" + getIntent().getStringExtra("selling") + "';"

                                );
                                // Set the click to being requested.
                                ((Button) findViewById(R.id.selected_interest)).setText(
                                        R.string.requested
                                );
                                requested = true;
                            }
                        }
                    }
            );
            this.<TextView>findViewById(R.id.selected_contact).setVisibility(View.GONE);
        }

        // Clicking on (x) returns to offers with pre-set fields.
        this.<FloatingActionButton>findViewById(R.id.selected_return).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        if (fromInterests) {
                            startActivity(new Intent(ContactDetails.this, OfferInterests.class)
                                    .putExtra("user", intent.getSerializableExtra("user"))
                                    .putExtra("offer", intent.getSerializableExtra("offer"))
                            );
                        } else {
                            startActivity(new Intent(ContactDetails.this, MainActivity.class)
                                    .putExtra("buying", intent.getStringExtra("selling"))
                                    .putExtra("selling", intent.getStringExtra("buying"))
                                    .putExtra("at", intent.getStringExtra("airport"))
                                    .putExtra("user", intent.getSerializableExtra("user"))
                                    .putExtra("requested", requested)
                            );

                        }
                    }
                }
        );
    }


}
