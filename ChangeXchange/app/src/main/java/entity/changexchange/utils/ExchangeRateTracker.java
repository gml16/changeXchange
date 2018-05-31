package entity.changexchange.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import entity.changexchange.R;

public class ExchangeRateTracker {

    public static double getExchangeRate(String fromCurrency, String toCurrency) {
        double result = -1;

        try {
            // Open a connection to bloomberg to get exchange rates
            URL bloombergCurrency = new URL("https://www.bloomberg.com/quote/" + fromCurrency + toCurrency + ":CUR");
            URLConnection bc = bloombergCurrency.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(bc.getInputStream()));

            String inputLine;  //Used to read in lines from webpage
            while ((inputLine = in.readLine()) != null && result == -1) {
                if(inputLine.length() > 18) {
                    if (inputLine.substring(0, 17).equals("bootstrappedData:")) {
                        result = Double.parseDouble(inputLine.substring(inputLine.indexOf("\"price\":")+8 , inputLine.indexOf("\"price\":")+14));
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
}
