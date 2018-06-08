package entity.changexchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import entity.changexchange.utils.Currency;
import entity.changexchange.utils.User;

public class Profile extends AppCompatActivity {

    private User user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        user = (User) getIntent().getSerializableExtra("user");

        //TODO: Fetch userdata from database and integrate correctly.
        //TODO: this.<ImageView>findViewById(R.id.profile_picture).setImageIcon();
        this.<TextView>findViewById(R.id.profile_name).setText(user.getName());
        this.<TextView>findViewById(R.id.profile_nickname).setText(user.getNickname());
        this.<TextView>findViewById(R.id.profile_fav_currency).setText(user.getPreferedCurrency().toString());
        this.<TextView>findViewById(R.id.profile_contact).setText(user.getPreferedContactDetails());

        // Clicking on edit brings up profile edit activity.
        this.<FloatingActionButton>findViewById(R.id.profile_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(Profile.this, EditProfile.class);
                editIntent.putExtra("user", user);
                startActivity(editIntent);
            }
        });
    }
}
