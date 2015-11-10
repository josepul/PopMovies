package es.josepul.popmovies.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import es.josepul.popmovies.FetchMovies;
import es.josepul.popmovies.util.Utils;

public class NetworkReceiver extends BroadcastReceiver {

    private static boolean firstConnect = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Utils.internetConnectionAvailable(cm)) {
            if(firstConnect) {
                // do subroutines here
                firstConnect = false;
                new FetchMovies(context, null).execute();
            }
        }
        else {
            firstConnect= true;
        }
    }
}