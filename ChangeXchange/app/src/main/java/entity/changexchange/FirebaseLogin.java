package entity.changexchange;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                String email = ((EditText) findViewById(R.id.Email)).getText().toString();
                String pwd = ((EditText) findViewById(R.id.Pwd)).getText().toString();
                if(!email.isEmpty() && !pwd.isEmpty()) {
                    createAccount(email, pwd);
                }
            }
        });

        final Button signIn = findViewById(R.id.SignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.Email)).getText().toString();
                String pwd = ((EditText) findViewById(R.id.Pwd)).getText().toString();
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
            startActivity(new Intent(FirebaseLogin.this, MainActivity.class));
        }
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user){
        //TODO
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("debugGuy", "istask a success");
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("debugGuy", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(FirebaseLogin.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("debugGuy", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signInUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("debugGuy", "Signing in");
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("debugGuy", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(FirebaseLogin.this, MainActivity.class));
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
