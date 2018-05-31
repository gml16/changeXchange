package entity.changexchange.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RequestDatabase extends AsyncTask<String, Void, Void> {

    private Exception exception;
    private List<Offer> offers;

    public RequestDatabase() {

        this.offers = new ArrayList<>();
    }

    public RequestDatabase(List<Offer> offers) {
        this.offers = offers;
    }

    protected Void doInBackground(String... strings) {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://db.doc.ic.ac.uk/g1727132_u",
                            "g1727132_u", "4ihe2mwvgy");
            c.setAutoCommit(false);
            Log.d("guy", "3");
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(strings[0]);
            if (strings[0].split(" ")[0].equals("SELECT")) {
                while (rs.next()) {
                    String nickname = rs.getString("nickname");
                    String buying = rs.getString("buying");
                    String selling = rs.getString("selling");
                    String amount = rs.getString("amount");
                    String location = rs.getString("location");

                    offers.add(new Offer(
                            nickname,
                            Currency.valueOf(buying),
                            Currency.valueOf(selling),
                            Float.valueOf(amount),
                            Airport.valueOf(location)
                    ));

                    for (Offer el : offers) {
                        //Log.d("guy", el.getPoster_nickname());
                    }
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            //System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            //System.exit(0);
        }
        return null;
    }

    protected void onPostExecute() {

    }

}
