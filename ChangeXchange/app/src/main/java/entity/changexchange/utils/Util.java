package entity.changexchange.utils;

import java.util.regex.Pattern;

public class Util {

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
                || !rfc2822.matcher(contact).matches();
        // TODO: Check for valid phone number.
    }
}
