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

        // Set the correct contact detail.
        this.<TextView>findViewById(R.id.selected_contact)
                .setText(getIntent().getStringExtra("CONTACT"));
        // TODO: Fetches the users' favored contact and displays accordingly.
//        new RequestDatabase(this.<TextView>findViewById(R.id.selected_contact));
    }


}
