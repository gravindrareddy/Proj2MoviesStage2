package redgun.moviesstage2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import redgun.moviesstage2.R;


/**
 * Created by gravi on 30-09-2016.
 */
public class Utility {
    /**
     * Method to display Toast message
     */
    public static void showToast(Context _context, String value) {
        if (value != null)
            Toast.makeText(_context, value, Toast.LENGTH_LONG).show();
    }

    public static boolean isTopSelected(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_top))
                .equals(context.getString(R.string.pref_sort_top));
    }

    public static boolean isOnline(Context _context) {
        ConnectivityManager cm =
                (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            showToast(_context, "No Internet!!");
        }
        return false;
    }
}
