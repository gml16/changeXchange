package entity.changexchange;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Offer;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

import static entity.changexchange.utils.Util.DATABASE_REQUEST_DELAY;
import static entity.changexchange.utils.Util.databaseWait;
import static entity.changexchange.utils.Util.filter;

public class OtherProfile extends AppCompatActivity {

    private User user;
    private boolean hide_contact;

    // Menu related fields.
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        // Setup menu
        setupMenu();

        // Get logged in user.
        user = (User) getIntent().getSerializableExtra("user");
        hide_contact = getIntent().getBooleanExtra("hide_contact", false);
        // Get the profile of the selected user
        String nickname = getIntent().getStringExtra("nickname");

        List<User> users = new ArrayList<>();
        new RequestDatabase(users).execute(
                "SELECT * FROM users WHERE nickname='" + nickname + "';"
        );
        databaseWait();
        updateView(users.get(0), user);
    }

    /**
     * Set's up the activity w.r.t. the user.
     */
    private void updateView(final User user, final User superUser) {
        // Set all fields according to their user.

        Button submit = findViewById(R.id.other_profile_add_rating);
        TextView nickname = findViewById(R.id.other_profile_nickname);
        TextView currency = findViewById(R.id.other_profile_fav_currency);
        TextView contact = findViewById(R.id.other_profile_contact);

        nickname.setText(user.getNickname());
        currency.setText(user.getCurrency().toString());
        contact.setText(
                hide_contact ? "Private" : user.getContact()
        );
        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: get ratings to work
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        RatingUser rater = new RatingUser();
                        rater.setUser(user);
                        rater.setSuperUser(superUser);
                        transaction.replace(android.R.id.content, rater);
                        transaction.commit();
                    }
                }
        );
    }

    /**
     * MENU RELATED METHODS
     */

    private void setupMenu() {
        drawerList = findViewById(R.id.navList);
        drawerLayout = findViewById(R.id.drawer_layout);
        title = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addDrawerItems() {
        String[] tabs = {"Offers", "Profile", "My Offers", "Messages", "Settings"};
        drawerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tabs);
        drawerList.setAdapter(drawerAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        startActivity(new Intent(OtherProfile.this, MainActivity.class)
                                .putExtra("user", user));
                        break;
                    case 1:
                        startActivity(new Intent(OtherProfile.this, Profile.class)
                                .putExtra("user", user));
                        break;
                    case 2:
                        startActivity(new Intent(OtherProfile.this, MyOffers.class)
                                .putExtra("user", user));
                        break;
                    case 3:
//                        startActivity(new Intent(MainActivity.this, Messages.class));
                        Toast.makeText(OtherProfile.this, "Messages coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
//                        startActivity(new Intent(MainActivity.this, Settings.class));
                        Toast.makeText(OtherProfile.this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.test, R.string.test) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("ChangeXchange");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
