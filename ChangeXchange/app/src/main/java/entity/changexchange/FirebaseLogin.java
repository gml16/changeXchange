package entity.changexchange;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import entity.changexchange.utils.Currency;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

public class FirebaseLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);
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
            String userEmail = currentUser.getEmail();
            List<User> listOfUsers = new ArrayList<>();
            new RequestDatabase(listOfUsers).execute("SELECT * FROM users WHERE login='" + userEmail + "';");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            User user = listOfUsers.get(0);
            startActivity(new Intent(FirebaseLogin.this, MainActivity.class).putExtra("user", user));
        }
    }


    private void signInUser(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("test", "succesful");
                            List<User> listOfUsers = new ArrayList<>();
                            new RequestDatabase(listOfUsers).execute("SELECT * FROM users WHERE login='" + email + "';");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            User user = listOfUsers.get(0);
                            startActivity(new Intent(FirebaseLogin.this, MainActivity.class).putExtra("user", user));
                        } else {
                            Log.d("test", "unsuccessful");
                            Toast.makeText(FirebaseLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
