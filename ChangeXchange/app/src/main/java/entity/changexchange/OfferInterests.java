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
import static entity.changexchange.utils.Util.filter;

public class OfferInterests extends AppCompatActivity {

    private User user;
    private Offer offer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_interests);

        user = (User) getIntent().getSerializableExtra("user");

        // Get the list of users from database.
        offer = (Offer) getIntent().getSerializableExtra("offer");
        ArrayList<Offer> offers = new ArrayList<>();
        new RequestDatabase(offers).execute(
                "SELECT * FROM offers WHERE "
                        + "nickname='" + offer.getPoster_nickname() + "' and "
                        + "buying='" + offer.getBuying() + "' and "
                        + "selling='" + offer.getSelling() + "' and "
                        + "amount='" + offer.getAmount() + "' and "
                        + "location='" + offer.getLocation() + "' and "
                        + "note='" + offer.getNote() + "';"
        );
        databaseWait();
        final List<String> interests = filter(offers.get(0).getInterests());

        // Set text to reflect the number of people interested.
        if (interests.isEmpty()) {
            this.<TextView>findViewById(R.id.interests_description).setVisibility(View.GONE);
        } else {
            this.<TextView>findViewById(R.id.interests_none).setVisibility(View.GONE);
            this.<TextView>findViewById(R.id.interests_description).setText(
                    interests.size() + " people are interested now!"
            );
        }

        // Get the list of interested users for this offer.
        this.<GridView>findViewById(R.id.interests_grid).setAdapter(
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, interests
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
                                .putExtra("interest", "true")
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
