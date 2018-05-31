package entity.changexchange;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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


}
