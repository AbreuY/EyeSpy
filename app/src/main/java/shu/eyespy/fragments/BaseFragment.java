package shu.eyespy.fragments;

import android.support.v4.app.Fragment;

//URL - https://www.skoumal.com/en/android-handle-back-press-in-fragment/
//Shows how to allow for backpress within a fragment.

public abstract class BaseFragment extends Fragment {

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    public abstract boolean onBackPressed();
}
