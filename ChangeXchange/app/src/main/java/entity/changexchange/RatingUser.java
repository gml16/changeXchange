package entity.changexchange;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import entity.changexchange.utils.RequestDatabase;
import entity.changexchange.utils.User;

import static entity.changexchange.utils.Util.NEG_THRESHOLD;
import static entity.changexchange.utils.Util.MAX_STAR;

public class RatingUser extends android.app.Fragment {

    private User user;
    private User superUser;
    private ViewGroup container;

    public void setUser(User user) {
        this.user = user;
    }

    public void setSuperUser(User superUser) {
        this.superUser = superUser;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView) container.findViewById(R.id.rating_user)).setText(
                "@" + user.getNickname()
        );
        container.findViewById(R.id.rating_confirm).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context ctx = v.getContext();
                        float mark = Float.parseFloat(
                                ((EditText) container.findViewById(R.id.rating_value))
                                        .getText().toString()
                        );
                        if (mark > MAX_STAR || mark < NEG_THRESHOLD) {
                            Toast.makeText(ctx, "Error: Rating must be 0.0 ≤ n ≤ 5.0.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        user.addRating(mark);
                        new RequestDatabase().execute(
                                "UPDATE users SET rating='" + user.getRating() + "', "
                                        + "num_ratings=num_ratings+1"
                                        + " WHERE nickname='" + user.getNickname() + "';"
                        );
                        ctx.startActivity(new Intent(ctx, MainActivity.class)
                                .putExtra("user", superUser));
                    }
                }
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = container;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rating_user, container, false);
    }
}
