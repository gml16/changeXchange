package entity.changexchange.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.R;

import static entity.changexchange.utils.Util.CONTACT;
import static entity.changexchange.utils.Util.RATING;

public class RequestDatabase extends AsyncTask<String, Void, Void> {

    private String table;
    private String instruction;
    private Exception exception;

    private User user;

    // For populating offers.
    private Activity activity;
    private List<Offer> offers = new ArrayList<>();

    // For fetching users from database.
    private List<User> users = new ArrayList<>();

    // For showing correct user details.
    private int type;
    private TextView textView;
    private TextView textViewBis;

    public RequestDatabase() {
    }

    public RequestDatabase(Activity activity, User user) {
        this.user = user;
        this.activity = activity;
    }

    public RequestDatabase(TextView textView, int type) {
        this.textView = textView;
        this.type = type;
    }

    public RequestDatabase(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    public RequestDatabase(List<User> users) {
        this.users = users;
    }

    public RequestDatabase(TextView rating, TextView num_rating, int type) {
        this.textView = rating;
        this.textViewBis = num_rating;
        this.type = type;
    }

    protected Void doInBackground(String... strings) {
        Connection c;
        Statement stmt;
        instruction = strings[0].split(" ")[0];
        table = strings[0].split(" ")[3];
        Log.d("test", strings[0]);

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://db.doc.ic.ac.uk/g1727132_u?&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory",
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
                                rs.getString("note"),
                                rs.getString("interested_users")));
                    } else if (table.equals("users")) {
                        users.add(new User(
                                rs.getString("nickname"),
                                Currency.valueOf(rs.getString("currency")),
                                rs.getString("contact"),
                                Double.valueOf(rs.getString("rating")),
                                Integer.valueOf(rs.getString("num_ratings")),
                                rs.getString("login"),
                                rs.getString("token")
                        ));
                    }

                }
                Log.d("test", "Found " + String.valueOf(users.size()));
                rs.close();
                stmt.close();
            } else if (instruction.equals("INSERT") || instruction.equals("DELETE")
                    || instruction.equals("UPDATE")) {
                stmt.executeUpdate(strings[0]);
                stmt.close();
            }
            c.commit();

            c.close();
        } catch (Exception e) {
            Log.d("Database", "Database error in RequestDatabase");
            Log.d("Database", e.getMessage());
        }
        return null;
    }

    protected void onPostExecute(Void unused) {
        if (instruction.equals("SELECT")) {
            if (table.equals("offers") && activity != null) {
                setupOffers();
            } else if (table.equals("users") && textView != null) {
                setupTextView();
            }
        }
    }

    /**
     * Sets the given TextView to the correct user data w.r.t. to the type flag.
     */
    private void setupTextView() {
        switch (type) {
            case CONTACT:
                textView.setText(users.get(0).getContact());
                break;
            case RATING:
                textView.setText(String.format("%.2f", users.get(0).getRating()) + "/5");
                if (textViewBis != null) {
                    int ratings = users.get(0).getNumRating();
                    textViewBis.setText(String.format(
                            "From %s review%s",
                            String.valueOf(ratings), ratings == 1 ? "" : "s")
                    );
                }
                break;
        }
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
        offer_container.setAdapter(new OfferAdapter(activity, offers, user));
    }

}
