package entity.changexchange.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import entity.changexchange.R;

public class ExchangeRateTracker extends AsyncTask<String, Void, Double> {

    private Exception exception;
    private TextView textToBeUpdated;
    private float selling;
    private Currency buying;

    public ExchangeRateTracker(TextView textToBeUpdated, float selling, Currency buying) {
        this.textToBeUpdated = textToBeUpdated;
        this.selling = selling;
        this.buying = buying;
    }

    public ExchangeRateTracker(TextView textToBeUpdated) {
        this.textToBeUpdated = textToBeUpdated;
    }

    protected Double doInBackground(String... strings) {
        double result = 0;

        if (strings[0].equals(strings[1])) {
            return 1.0;
        }

        try {
            // Open a connection to bloomberg to get exchange rates
            URL bloombergCurrency = new URL("https://www.bloomberg.com/quote/" + strings[0] + strings[1] + ":CUR");
            URLConnection bc = bloombergCurrency.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(bc.getInputStream()));

            String inputLine;  //Used to read in lines from webpage
            while ((inputLine = in.readLine()) != null && result == 0) {
                if (inputLine.length() > 18) {
                    if (inputLine.substring(0, 17).equals("bootstrappedData:")) {
                        result = Double.parseDouble(inputLine.substring(inputLine.indexOf("\"price\":") + 8, inputLine.indexOf("\"price\":") + 14));
                    }
                }

            }
            in.close(); //DONE. Closing connection.

        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException in getExchangeRate(): Invalid URL.");
        } catch (NumberFormatException ex) {
            System.out.println("NumberFormatException in getExchangeRate(): Invalid response from server.");
        } catch (IOException ex) {
            System.out.println("IOException in getExchangeRate(): Cannot connect to server.");
        }

        return result;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    protected void onPostExecute(Double result) {
        textToBeUpdated.setText(
                textToBeUpdated.getId() == R.id.offer_amount_recieve ?
                        // Inside an offer.
                        String.format("%.2f", selling * result) + " " + buying :
                        // Showing the global exchange rate.
                        "1:" + String.valueOf(result)
        );
    }
}
