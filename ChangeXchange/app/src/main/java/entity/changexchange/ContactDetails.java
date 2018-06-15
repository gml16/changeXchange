package entity.changexchange;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import entity.changexchange.utils.RequestDatabase;

public class ContactDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        // Fetch the poster's preferred contact detail of the poster.
        new RequestDatabase(this.<TextView>findViewById(R.id.selected_contact))
                .execute(
                        "SELECT * FROM users WHERE nickname='"
                                + getIntent().getStringExtra("nickname") + "';"
                );

        // Clicking on (x) returns to offers with pre-set fields.
        this.<FloatingActionButton>findViewById(R.id.selected_return).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        startActivity(new Intent(ContactDetails.this, MainActivity.class)
                                .putExtra("from", intent.getStringExtra("selling"))
                                .putExtra("to", intent.getStringExtra("buying"))
                                .putExtra("at", intent.getStringExtra("airport"))
                                .putExtra("user", intent.getSerializableExtra("user"))
                        );
                    }
                }
        );
    }


}
