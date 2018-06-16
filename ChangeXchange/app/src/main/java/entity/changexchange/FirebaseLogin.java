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

import entity.changexchange.utils.Currency;
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
                if(!email.isEmpty() && !pwd.isEmpty()) {
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
        if(currentUser != null){
            User ourUser = new User("gml16", Currency.GBP, "guy.leroy99@gmail.com", 4.8);
            startActivity(new Intent(FirebaseLogin.this, MainActivity.class).putExtra("user", ourUser));
        }
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user){
        //TODO
    }


    private void signInUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("debugGuy", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            User ourUser = new User("Guy", Currency.GBP, "guy.leroy99@gmail.com", 4.8);
                            startActivity(new Intent(FirebaseLogin.this, MainActivity.class).putExtra("user", ourUser));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("debugGuy", "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
