package entity.changexchange;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

public class EditProfile extends AppCompatActivity {

    private static final char[] ESCAPES = {'\r', '\t', '\n'};
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = (User) getIntent().getSerializableExtra("user");

        this.<Button>findViewById(R.id.edit_profile_confirm).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newNickname = filter(
                                ((EditText)findViewById(R.id.change_nickname_edittext)).getText().toString()
                        );
                        if(!newNickname.isEmpty()) {
                            user.changeNickname(newNickname);
                        }
                        startActivity(
                                new Intent(EditProfile.this, Profile.class)
                                        .putExtra("user", user)
                        );
                        // TODO: Update current interaction so that update is propagated to the
                        // TODO: database, rather than through an intent.
//                        new RequestDatabase().execute(
//                                "UPDATE users SET nickname=" + newNickname
//                                + " WHERE nickname=" + user.getNickname()
//                        );
                    }
                }
        );
    }

    /**
     * Removes illegal characters from user input before parsing.
     */
    private String filter(String userInput) {
        for (char c : ESCAPES) {
            userInput = userInput.replace(c, ' ');
        }
        return userInput;
    }
}
