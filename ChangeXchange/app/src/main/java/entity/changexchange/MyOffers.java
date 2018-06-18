package entity.changexchange;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import entity.changexchange.utils.Currency;
import entity.changexchange.utils.Offer;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

public class MyOffers extends AppCompatActivity {

    // Menu related fields.
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ArrayAdapter<String> drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private String title;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers);

        user = (User) getIntent().getSerializableExtra("user");

        // Menu setup.
        setupMenu();

        // Setup offers.
        updateMyOffers();

        final SwipeRefreshLayout layout = findViewById(R.id.my_offers_swiper);

        // Pulling offers down will refresh them.
        layout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateMyOffers();
                        layout.setRefreshing(!layout.isRefreshing());
                    }
                }
        );
    }

    private void updateMyOffers() {
        // Show offers
        new RequestDatabase(this, user).execute(
                "SELECT * FROM offers WHERE nickname='" + user.getNickname()
                        + "' ORDER BY amount;"
        );
    }

    /**
     * MENU RELATED METHODS
     */

    private void setupMenu() {
        // Menu setup.
        drawerList = findViewById(R.id.navList);
        drawerLayout = findViewById(R.id.drawer_layout);
        title = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addDrawerItems() {
        String[] tabs = {"Offers", "Profile", "My Offers", "Messages", "Find friends", "Settings"};
        drawerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, tabs
        );
        drawerList.setAdapter(drawerAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MyOffers.this, MainActivity.class)
                                .putExtra("user", user));

                        break;
                    case 1:
                        startActivity(new Intent(MyOffers.this, Profile.class)
                                .putExtra("user", user));
                        break;
                    case 2:
                        // Do nothing.
                        Toast.makeText(MyOffers.this, "Already in My Offers!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
//                        startActivity(new Intent(MainActivity.this, Messages.class));
                        Toast.makeText(MyOffers.this, "Messages coming soon!", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        startActivity(new Intent(MyOffers.this, OtherProfile.class)
                                .putExtra("user", user));
                        break;
                    case 5:
//                        startActivity(new Intent(MainActivity.this, Settings.class));
                        Toast.makeText(MyOffers.this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
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
