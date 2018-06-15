package entity.changexchange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import entity.changexchange.utils.RequestDatabase;

public class sendText extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        // Fetch the poster's preferred contact detail of the poster.
        new RequestDatabase(this.<TextView>findViewById(R.id.selected_contact))
                .execute(
                        "SELECT * FROM users WHERE nickname='"
                                + getIntent().getStringExtra("nickname") + "';"
                );
    }


}
