package entity.changexchange;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import entity.changexchange.utils.User;

public class EditProfile extends AppCompatActivity {

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
                        String newNickname = ((EditText)findViewById(R.id.change_nickname_edittext)).getText().toString();
                        if(!newNickname.isEmpty()) {
                            user.changeNickname(newNickname);
                        }
                        Intent profileIntent = new Intent(EditProfile.this, Profile.class);
                        profileIntent.putExtra("user", user);
                        startActivity(profileIntent);
                    }
                }
        );
    }
}
