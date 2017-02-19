package gr.escsoft.michaelprimez.searchablespinner.interfaces;

import android.view.View;

/**
 * Created by michael on 2/12/17.
 */

public interface ISpinnerSelectedView {
    View getNoSelectionView();
    View getSelectedView(int position);
}
