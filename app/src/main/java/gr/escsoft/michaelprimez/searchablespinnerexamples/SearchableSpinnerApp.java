package gr.escsoft.michaelprimez.searchablespinnerexamples;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by michael on 1/8/17.
 */

public class SearchableSpinnerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
