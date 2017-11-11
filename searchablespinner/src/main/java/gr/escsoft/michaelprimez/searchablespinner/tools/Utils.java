package gr.escsoft.michaelprimez.searchablespinner.tools;

import android.os.Build;

/**
 * Created by akhil on 11-11-2017.
 */

public class Utils {

    public static boolean isLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
