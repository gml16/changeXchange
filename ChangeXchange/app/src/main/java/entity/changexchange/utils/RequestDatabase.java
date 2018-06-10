package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.MainActivity;
import entity.changexchange.Profile;
import entity.changexchange.R;

public class RequestDatabase extends AsyncTask<String, Void, Void> {

    private String table;
    private String instruction;
    private Exception exception;

    // For populating offers.
    private MainActivity activity;
    private List<Offer> offers;

    // For populating the profile.
    private Profile profile;
    private User user;

    // For showing correcut contact detail.
    private TextView textView;

    public RequestDatabase() {
        this.offers = new ArrayList<>();
    }

    public RequestDatabase(List<Offer> offers) {
        this.offers = offers;
    }

    public RequestDatabase(Profile profile) {
        this.profile = profile;
    }

    public RequestDatabase(MainActivity activity) {
        this.activity = activity;
        offers = new ArrayList<>();
    }

    public RequestDatabase(TextView textView) {
        this.textView = textView;
    }

    protected Void doInBackground(String... strings) {
        Connection c = null;
        Statement stmt = null;
        instruction = strings[0].split(" ")[0];
        table = strings[0].split(" ")[3];

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://db.doc.ic.ac.uk/g1727132_u",
                            "g1727132_u", "4ihe2mwvgy");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            if (instruction.equals("SELECT")) {
                ResultSet rs = stmt.executeQuery(strings[0]);
                while (rs.next()) {

                    if (table.equals("offers")) {
                        offers.add(new Offer(
                                rs.getString("nickname"),
                                Currency.valueOf(rs.getString("buying")),
                                Currency.valueOf(rs.getString("selling")),
                                Float.valueOf(rs.getString("amount")),
                                Airport.valueOf(rs.getString("location")),
                                rs.getString("note")
                        ));
                    } else if (table.equals("users")) {
                        user = new User(
                                rs.getString("name"),
                                rs.getString("nickname"),
                                Currency.valueOf(rs.getString("currency")),
                                rs.getString("contact")
                        );
                    }

                }
                rs.close();
                stmt.close();
            } else if(instruction.equals("INSERT") || instruction.equals("UPDATE")){
                stmt.executeUpdate(strings[0]);
                stmt.close();
                c.commit();
            }

            c.close();
        } catch (Exception e) {
            //System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            //System.exit(0);
        }
        return null;
    }

    protected void onPostExecute(Void unused) {
        if (instruction.equals("SELECT")) {
            if (table.equals("offers")) {
                setupOffers();

            } else if (table.equals("users")) {
                if (profile != null) {
                    setupProfile();
                } else {
                    textView.setText(user.getPreferredContactDetails());
                }
            }
        }
    }

    /**
     * Setup the profile page w.r.t. the database data.
     */
    private void setupProfile() {
        ((TextView) profile.findViewById(R.id.profile_name)).setText(
                user.getName()
        );
        ((TextView) profile.findViewById(R.id.profile_nickname)).setText(
                user.getNickname()
        );
        ((TextView) profile.findViewById(R.id.profile_fav_currency)).setText(
                user.getPreferredCurrency().toString()
        );
        ((TextView) profile.findViewById(R.id.profile_contact)).setText(
                user.getPreferredContactDetails()
        );
    }

    /**
     * Setup the offers collected from database to MainActivity.
     */
    private void setupOffers() {
        RecyclerView offer_container = activity.findViewById(R.id.offer_container);
        offer_container.setHasFixedSize(true);
        offer_container.setLayoutManager(new LinearLayoutManager(activity));
        offer_container.setAdapter(new OfferAdapter(activity, offers));
    }

}
