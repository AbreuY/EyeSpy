package shu.apps.eyespy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import shu.apps.eyespy.R;

public class MainMenuFragment extends Fragment implements OnClickListener {

    private static final String TAG = MainMenuFragment.class.getSimpleName();
    private String mUsername;
    private Listener mListener = null;

    private TextView mUsernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        final int[] clickableIds = new int[]{
                R.id.main_menu_profile_button,
                R.id.main_menu_play_button,
                R.id.main_menu_trophies_button,
                R.id.main_menu_rankings_button,
                R.id.main_menu_options_button
        };

        for (int clickableId : clickableIds) {
            view.findViewById(clickableId).setOnClickListener(this);
        }

        mUsernameTextView = view.findViewById(R.id.main_menu_username_text_view);

        return view;
    }

    public void setUsername(String username) {
        this.mUsername = username;
        updateUI();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.getId());
        switch (view.getId()) {
            case R.id.main_menu_profile_button:
                mListener.onProfilePageRequested();
                break;
            case R.id.main_menu_play_button:
                mListener.onStartGameRequested();
                break;
            case R.id.main_menu_trophies_button:
                mListener.onShowAchievementsRequested();
                break;
            case R.id.main_menu_rankings_button:
                mListener.onShowLeaderboardRequested();
                break;
            case R.id.main_menu_options_button:
                mListener.onShowSettingsRequested();
        }
    }

    private void updateUI() {
        mUsernameTextView.setText(mUsername);
    }

    public interface Listener {
        void onProfilePageRequested();

        void onStartGameRequested();

        void onShowAchievementsRequested();

        void onShowLeaderboardRequested();

        void onShowSettingsRequested();
    }
}
