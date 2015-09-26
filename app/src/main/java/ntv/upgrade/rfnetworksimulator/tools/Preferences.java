package ntv.upgrade.rfnetworksimulator.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ntv.upgrade.rfnetworksimulator.R;

/**
 * Created by Paulino on 9/25/2015.
 */
public class Preferences {

    public static String getPreferredMapStyle(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_key_map_style),
                context.getString(R.string.pref_default_map_style));
    }

    public static boolean getPreferenceDummyData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(
                context.getString(R.string.pref_key_load_dummy_data),
                Boolean.parseBoolean(context.getString(R.string.pref_default_load_dummy_data)));
    }


}
