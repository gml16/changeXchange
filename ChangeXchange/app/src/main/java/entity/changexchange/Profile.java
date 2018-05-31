package entity.changexchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import entity.changexchange.utils.Currency;

public class Profile extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //TODO: Fetch userdata from database and integrate correctly.
        //TODO: this.<ImageView>findViewById(R.id.profile_picture).setImageIcon();
        this.<TextView>findViewById(R.id.profile_name).setText("John Test");
        this.<TextView>findViewById(R.id.profile_nickname).setText("Jenkins");
        this.<TextView>findViewById(R.id.profile_fav_currency).setText(Currency.USD.toString());
        this.<TextView>findViewById(R.id.profile_email).setText("johntest@test.com");
        this.<TextView>findViewById(R.id.profile_phone_num).setText("+44 (0) 1234 567890");

        // Switch to Messages / Profile activity.
        this.<BottomNavigationView>findViewById(R.id.navigation).setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_profile:
                        // Do nothing.
                        break;
                    case R.id.navigation_offers:
                        startActivity(new Intent(Profile.this, MainActivity.class));
                        break;
                    case R.id.navigation_messages:
                        //TODO:startActivity(new Intent(MainActivity.this, Messages.class));
                        break;
                }
                return false;
            }
        });

        // Clicking on edit brings up profile edit activity.
        this.<FloatingActionButton>findViewById(R.id.profile_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, EditProfile.class));
            }
        });
    }
}
