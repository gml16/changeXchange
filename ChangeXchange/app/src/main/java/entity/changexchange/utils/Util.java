package entity.changexchange.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Pattern;

import entity.changexchange.R;

import static entity.changexchange.utils.Currency.GBP;

public class Util {

    // Limits for currency entries.
    public static float NEG_THRESHOLD = (float) 0.001;
    public static float MAX_AMOUNT = (float) 50;

    // Approximate time a database query takes.
    public static final int DATABASE_REQUEST_DELAY = 200;

    // Flags so that the database knows what kind of user data we want.
    public static final int CONTACT = 0;
    public static final int RATING = 1;

    // Illegal characters for nicknames.
    private static final char[] ILLEGALS = {'\r', '\t', '\n'};

    // Pattern for verifying syntax email.
    private static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );

    /**
     * Removes illegal characters from user input before parsing.
     */
    public static String filter(String string) {

        for (char c : ILLEGALS) {
            string = string.replace(c, ' ');
        }
        return string.replaceAll("'", "\\\'");
    }

    /**
     * Checks that the given contact is a valid email address / phone number / or AppMessaging.
     */
    public static boolean isInvalid(String contact) {
        return contact.equals("In app")
                || !rfc2822.matcher(contact).matches() || contact.isEmpty();
        // TODO: Check for valid phone number.
    }

    /**
     * Gets the GBP value of the given currency.
     */
    public static float gbpEquivalenceAmount (float amount, String buying, View view) {
        TextView rate = view.findViewById(R.id.hidden_val);
        new ExchangeRateTracker(rate).execute(
                buying, GBP.toString()
        );
        databaseWait();
        return amount * Float.valueOf(rate.getText().toString());
    }

    /**
     * Sleeps thread for some amount of time - Essentially to allow the database work to complete.
     */
    public static void databaseWait() {
        try {
            Thread.sleep(DATABASE_REQUEST_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
