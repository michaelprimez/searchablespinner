package gr.escsoft.michaelprimez.searchablespinner.tools;

import android.content.Context;

/**
 * Created by michael on 12/31/16.
 */

public class UITools {

    private UITools() { }

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }
}
