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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import entity.changexchange.utils.Currency;
import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;


public class FirebaseSignup extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_signup);
        mAuth = FirebaseAuth.getInstance();

        final Button signUp = findViewById(R.id.SignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.EmailEditText)).getText().toString();
                String pwd = ((EditText) findViewById(R.id.PwdEditText)).getText().toString();
                String nickname = ((EditText) findViewById(R.id.NicknameEditText)).getText().toString();
                String contact = ((EditText) findViewById(R.id.ContactEditText)).getText().toString();
                if (!email.isEmpty() && !pwd.isEmpty() && !nickname.isEmpty() && !contact.isEmpty()) {
                    createAccount(email, pwd, nickname, contact);
                }
            }
        });

        final Button signIn = findViewById(R.id.SignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(FirebaseSignup.this, FirebaseLogin.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(FirebaseSignup.this, MainActivity.class));
        }
    }

    private void createAccount(final String email, String password, final String nickname, final String contact) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String token = FirebaseInstanceId.getInstance().getToken();
                            Log.d("test", "Creating an account, the token is " + token);
                            new RequestDatabase().execute("INSERT INTO users VALUES ('" + nickname + "', 'GBP', '" + contact + "', 5, 1, '" + email +  "', '" + token +  "');");
                            User ourUser = new User(nickname, Currency.GBP, contact, 5, 1, email, token);
                            startActivity(new Intent(FirebaseSignup.this, MainActivity.class).putExtra("user", ourUser));
                        } else {
                            Toast.makeText(FirebaseSignup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
