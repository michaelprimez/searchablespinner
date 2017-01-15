package gr.escsoft.michaelprimez.searchablespinner;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by michael on 1/14/17.
 */

public class SelectedView {
    private View mView;
    private int mPosition;
    private @IdRes long mId;

    public SelectedView(View view, int position, @IdRes long id) {
        mView = view;
        mPosition = position;
        mId = id;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public long getId() {
        return mId;
    }

    public void setId(@IdRes long id) {
        mId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectedView that = (SelectedView) o;

        if (mPosition != that.mPosition) return false;
        if (mId != that.mId) return false;
        return mView != null ? mView.equals(that.mView) : that.mView == null;

    }

    @Override
    public int hashCode() {
        int result = mView != null ? mView.hashCode() : 0;
        result = 31 * result + mPosition;
        result = 31 * result + (int) (mId ^ (mId >>> 32));
        return result;
    }
}
