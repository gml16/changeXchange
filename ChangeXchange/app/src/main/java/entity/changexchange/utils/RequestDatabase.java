package entity.changexchange.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.MainActivity;
import entity.changexchange.MyOffers;
import entity.changexchange.R;

public class RequestDatabase extends AsyncTask<String, Void, Void> {

    private String table;
    private String instruction;
    private Exception exception;

    // For populating offers.
    private Activity activity;
    private List<Offer> offers;

    // For populating the profile.
    private User user;

    // For showing correcut contact detail.
    private TextView textView;

    public RequestDatabase() {
        this.offers = new ArrayList<>();
    }

    public RequestDatabase(List<Offer> offers) {
        this.offers = offers;
    }

    public RequestDatabase(Activity activity) {
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
                if (activity instanceof MyOffers || activity instanceof MainActivity) {
                    // We want to filter outdated offers first, hence UPDATE will launch the
                    // PostgreSQL adequate trigger.
                    stmt.executeUpdate(
                            "UPDATE offers SET nickname='dummy' WHERE nickname='dummy'"
                    );
                    stmt.close();
                    stmt = c.createStatement();
                }
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
                                rs.getString("contact"),
                                Double.valueOf(rs.getString("rating"))
                        );
                    }

                }
                rs.close();
                stmt.close();
            } else if (instruction.equals("INSERT") || instruction.equals("UPDATE")) {
                stmt.executeUpdate(strings[0]);
                stmt.close();
            }
            c.commit();

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
                if (activity != null) {
                    setupProfile();
                } else {
                    textView.setText(user.getContact());
                }
            }
        }
    }

    /**
     * Setup the profile page w.r.t. the database data.
     */
    private void setupProfile() {
        ((TextView) activity.findViewById(R.id.profile_name)).setText(
                user.getName()
        );
        ((TextView) activity.findViewById(R.id.profile_nickname)).setText(
                user.getNickname()
        );
        ((TextView) activity.findViewById(R.id.profile_fav_currency)).setText(
                user.getCurrency().toString()
        );
        ((TextView) activity.findViewById(R.id.profile_contact)).setText(
                user.getContact()
        );
    }

    /**
     * Setup the offers collected from database to MainActivity.
     */
    private void setupOffers() {
        RecyclerView offer_container = activity.findViewById(R.id.offer_container);
        if (offer_container == null) {
            offer_container = activity.findViewById(R.id.my_offer_container);
        }
        offer_container.setHasFixedSize(true);
        offer_container.setLayoutManager(new LinearLayoutManager(activity));
        offer_container.setAdapter(new OfferAdapter(activity, offers));
    }

}
