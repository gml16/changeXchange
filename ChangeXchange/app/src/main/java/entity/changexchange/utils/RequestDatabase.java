package entity.changexchange.utils;

import android.app.Activity;
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

import entity.changexchange.R;

import static entity.changexchange.utils.Util.CONTACT;
import static entity.changexchange.utils.Util.RATING;

public class RequestDatabase extends AsyncTask<String, Void, Void> {

    private String table;
    private String instruction;
    private Exception exception;

    // For populating offers.
    private Activity activity;
    private List<Offer> offers;

    // For fetching users from database.
    private List<User> users;

    // For showing correct user details.
    private int type;
    private TextView textView;

    public RequestDatabase() {
        this.offers = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public RequestDatabase(Activity activity) {
        this.activity = activity;
        offers = new ArrayList<>();
    }

    public RequestDatabase(TextView textView, int type) {
        this.textView = textView;
        this.type = type;
    }

    public RequestDatabase(List<User> users) {
        this.users = users;
    }

    protected Void doInBackground(String... strings) {
        Connection c;
        Statement stmt;
        instruction = strings[0].split(" ")[0];
        table = strings[0].split(" ")[3];

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
                                rs.getString("note")
                        ));
                    } else if (table.equals("users")) {
                        users.add(new User(
                                rs.getString("nickname"),
                                Currency.valueOf(rs.getString("currency")),
                                rs.getString("contact"),
                                Double.valueOf(rs.getString("rating"))
                        ));
                    }

                }
                rs.close();
                stmt.close();
            } else if (instruction.equals("INSERT") || instruction.equals("UPDATE")
                    || instruction.equals("DELETE")) {
                stmt.executeUpdate(strings[0]);
                stmt.close();
            }
            c.commit();

            c.close();
        } catch (Exception e) {
            Log.d("Database", "Database error in RequestDatabase");
        }
        return null;
    }

    protected void onPostExecute(Void unused) {
        if (instruction.equals("SELECT")) {
            if (table.equals("offers")) {
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
                textView.setText(String.valueOf(users.get(0).getRating()));
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
        offer_container.setAdapter(new OfferAdapter(activity, offers));
    }

}
