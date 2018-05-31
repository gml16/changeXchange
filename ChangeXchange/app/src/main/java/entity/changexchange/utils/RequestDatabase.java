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
            stmt = c.createStatement();
            if (strings[0].split(" ")[0].equals("SELECT")) {
                ResultSet rs = stmt.executeQuery(strings[0]);
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

                }
                rs.close();
                stmt.close();
            } else if(strings[0].split(" ")[0].equals("INSERT")){
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

    protected void onPostExecute() {

    }

}
