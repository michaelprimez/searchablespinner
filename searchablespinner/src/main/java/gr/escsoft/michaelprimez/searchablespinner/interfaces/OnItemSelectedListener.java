package gr.escsoft.michaelprimez.searchablespinner.interfaces;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by michael on 1/14/17.
 */

public interface OnItemSelectedListener {
    void onItemSelected(View view, int position, long id);
    void onNothingSelected();
}
