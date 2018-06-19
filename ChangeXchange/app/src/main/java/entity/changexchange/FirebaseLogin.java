package entity.changexchange;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Airport;
import entity.changexchange.utils.Currency;
import entity.changexchange.utils.Offer;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

import static entity.changexchange.utils.Util.databaseWait;

public class FirebaseLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);

        createNotificationChannel();

        mAuth = FirebaseAuth.getInstance();

        final Button signUp = findViewById(R.id.SignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(FirebaseLogin.this, FirebaseSignup.class));
            }
        });

        final Button signIn = findViewById(R.id.SignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.EmailEditText)).getText().toString();
                String pwd = ((EditText) findViewById(R.id.PwdEditText)).getText().toString();
                if (!email.isEmpty() && !pwd.isEmpty()) {
                    signInUser(email, pwd);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            final String userEmail = currentUser.getEmail();
            actualLogin(userEmail);
        }
    }


    private void signInUser(final String userEmail, String password) {
        mAuth.signInWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("test", "succesful");
                            actualLogin(userEmail);
                        } else {
                            Log.d("test", "unsuccessful");
                            Toast.makeText(FirebaseLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void actualLogin(final String userEmail){
        new AsyncTask<String, Void, Void>() {
            protected Void doInBackground(String... strings) {
                Connection c;
                Statement stmt;
                User user = null;
                try {
                    Class.forName("org.postgresql.Driver");
                    c = DriverManager
                            .getConnection("jdbc:postgresql://db.doc.ic.ac.uk/g1727132_u?&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory",
                                    "g1727132_u", "4ihe2mwvgy");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE login='" + userEmail + "';");
                    while (rs.next() && user == null) {
                        user = new User(
                                rs.getString("nickname"),
                                Currency.valueOf(rs.getString("currency")),
                                rs.getString("contact"),
                                Double.valueOf(rs.getString("rating")),
                                Integer.valueOf(rs.getString("num_ratings")),
                                rs.getString("login"),
                                rs.getString("token")
                        );
                    }
                    //TODO: verify whether token has changed and if so update it on the database
                    rs.close();
                    stmt.close();
                    c.commit();
                    c.close();
                    startActivity(new Intent(FirebaseLogin.this, MainActivity.class).putExtra("user", user));
                } catch (Exception e) {
                    Log.d("test", "Error Connecting");
                    Log.d("test", e.getMessage());
                }
                return null;
            }

        }.execute();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "main_channel";
            String description = "This is the channel for all notifications, for now.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("main_channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
