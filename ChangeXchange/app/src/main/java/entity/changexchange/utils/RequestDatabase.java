package entity.changexchange.utils;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.MainActivity;
import entity.changexchange.R;

public class RequestDatabase extends AsyncTask<String, Void, Void> {

    private Exception exception;
    private List<Offer> offers;
    private MainActivity activity;
    private String instruction;

    public RequestDatabase() {

        this.offers = new ArrayList<>();
    }

    public RequestDatabase(List<Offer> offers) {
        this.offers = offers;
    }

    public RequestDatabase(MainActivity activity) {
        this.activity = activity;
        offers = new ArrayList<>();
    }

    protected Void doInBackground(String... strings) {
        Connection c = null;
        Statement stmt = null;
        instruction = strings[0].split(" ")[0];
        Log.d("test", strings[0]);
        Log.d("test", "["+instruction+"]");

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://db.doc.ic.ac.uk/g1727132_u",
                            "g1727132_u", "4ihe2mwvgy");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            if (instruction.equals("SELECT")) {
                Log.d("guy", "selecting: " + strings[0]);
                ResultSet rs = stmt.executeQuery(strings[0]);
                while (rs.next()) {

                    offers.add(new Offer(
                            rs.getString("nickname"),
                            Currency.valueOf(rs.getString("buying")),
                            Currency.valueOf(rs.getString("selling")),
                            Float.valueOf(rs.getString("amount")),
                            Airport.valueOf(rs.getString("location")),
                            rs.getString("note")));

                }
                rs.close();
                stmt.close();
            } else if(instruction.equals("INSERT")){
                Log.d("guy", "inserting: " + strings[0]);
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
        // Setup container for offers.
        if (instruction.equals("SELECT")) {
            RecyclerView offer_container = activity.findViewById(R.id.offer_container);
            offer_container.setHasFixedSize(true);
            offer_container.setLayoutManager(new LinearLayoutManager(activity));
            offer_container.setAdapter(new OfferAdapter(activity, offers));
        }
    }

}
