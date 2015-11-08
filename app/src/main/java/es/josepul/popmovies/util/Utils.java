package es.josepul.popmovies.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Jose on 13/07/2015.
 */
public class Utils {

    /**
     * Check if there's internet connection or is entablishing connection
     * @param connectivityManager Connectivity manager of activity
     * @return true if has connection or entablishing connection
     */
    public static boolean internetConnectionAvailable(ConnectivityManager connectivityManager) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Check if a string is null or empty
     * @param string String to check
     * @return true if is null or empty, false otherwise
     */
    public static boolean isEmptyString(String string){
        return string == null || string.trim().length() == 0;
    }

}
