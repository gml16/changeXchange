package entity.changexchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Offer;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

import static entity.changexchange.utils.Util.databaseWait;

public class OfferInterests extends AppCompatActivity {

    private User user;
    private Offer offer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_interests);

        user = (User) getIntent().getSerializableExtra("user");
        offer = (Offer) getIntent().getSerializableExtra("offer");
        final List<String> interests = offer.getInterests();

        // Set text to reflect the number of people interested.
        this.<TextView>findViewById(R.id.interests_description).setText(
                interests.isEmpty() ? "We haven't registered any interest yet..."
                        : interests.size() + " people are interested now!"
        );

        // Get the list of interested users for this offer.
        this.<GridView>findViewById(R.id.interests_grid).setAdapter(
                new ArrayAdapter<>(
                        this, R.layout.activity_offer_interests, interests
                )
        );
        this.<GridView>findViewById(R.id.interests_grid).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(OfferInterests.this, ContactDetails.class)
                                .putExtra("user", user)
                                .putExtra("nickname", interests.get(position))
                                .putExtra("offer", offer)
                        );
                    }
                }
        );

        // Clicking on (x) returns to offers with pre-set fields.
        this.<FloatingActionButton>findViewById(R.id.interests_return).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        startActivity(new Intent(OfferInterests.this, MyOffers.class)
                                .putExtra("user", intent.getSerializableExtra("user"))
                        );
                    }
                }
        );
    }
}
