package shu.apps.eyespy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import shu.apps.eyespy.R;

public class SplashScreenFragment extends Fragment {

    private View mView;

    private String mStatus;
    private int mProgressVisibility;

    private TextView mStatusTextView;
    private ProgressBar mProgressBar;
    private Button mSignInRetryButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        mStatusTextView = mView.findViewById(R.id.splash_screen_status_text_view);
        mProgressBar = mView.findViewById(R.id.splash_screen_progress);

        return mView;
    }

    public void setStatus(String status, int progressVisibility) {
        mStatus = status;
        mProgressVisibility = progressVisibility;

        updateUI();
    }


    public void updateUI() {
        if (mView == null) {
            return;
        }

        this.mStatusTextView.setText(mStatus);
        this.mProgressBar.setVisibility(mProgressVisibility);
    }
}
