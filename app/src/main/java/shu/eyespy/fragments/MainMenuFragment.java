package shu.eyespy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import shu.eyespy.MainActivity;
import shu.eyespy.R;
import shu.eyespy.SoundManager;

public class MainMenuFragment extends Fragment implements OnClickListener {

    private static final String TAG = MainMenuFragment.class.getSimpleName();
    private View mView;
    private String mUsername;
    private Listener mListener = null;
    private SoundManager mSoundManager;

    private TextView mUsernameTextView;
    private TextView mScoreTextView;
    private TextView mAchievementCountTextView;

    private int achievementCount;
    private long score;

    boolean musicActive;
    boolean soundAcitve;

    public void setSoundManager(SoundManager soundManager) {
        this.mSoundManager = soundManager;

        updateUI();
    }

    public void setScore(long score) {
        this.score = score;

        updateUI();
    }

    public void setAchievementCount(int achievementCount) {
        this.achievementCount = achievementCount;

        updateUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        final int[] clickableIds = new int[]{
                R.id.main_menu_profile_button,
                R.id.main_menu_play_button,
                R.id.main_menu_trophies_button,
                R.id.main_menu_rankings_button,
                R.id.main_menu_options_button,
                R.id.soundImageView,
                R.id.musicImageView
        };

        for (int clickableId : clickableIds) {
            mView.findViewById(clickableId).setOnClickListener(this);
        }

        mUsernameTextView = mView.findViewById(R.id.main_menu_username_text_view);
        mScoreTextView = mView.findViewById(R.id.main_menu_score_text_view);
        mAchievementCountTextView = mView.findViewById(R.id.main_menu_achievement_text_view);

        updateUI();

        return mView;
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
                try {
                    mListener.onStartGameRequested();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.main_menu_trophies_button:
                mListener.onShowAchievementsRequested();
                break;
            case R.id.main_menu_rankings_button:
                mListener.onShowLeaderboardRequested();
                break;
            case R.id.main_menu_options_button:
                mListener.onShowSettingsRequested();
                break;
            case R.id.musicImageView:
                mListener.onMusicToggled();
                break;
            case R.id.soundImageView:
                mListener.onSoundToggled();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (mView == null) {
            return;
        }

        mUsernameTextView.setText(mUsername);
        mScoreTextView.setText(Long.toString(score));
        mAchievementCountTextView.setText(Integer.toString(achievementCount));
        updateSoundButton(mSoundManager.getSoundStatus());
        updateMusicButton(mSoundManager.getMusicStatus());
    }

    public void updateSoundButton(boolean active) {
        if (mView == null) {
            return;
        }

        ImageView soundButton =  mView.findViewById(R.id.soundImageView);
        if (active) {
            soundButton.setImageResource(R.drawable.sound_on);
        }
        else {
            soundButton.setImageResource(R.drawable.sound_off);
        }
    }

    public void updateMusicButton(boolean active) {
        if (mView == null) {
            return;
        }

        ImageView musicButton =  mView.findViewById(R.id.musicImageView);
        if (active) {
            musicButton.setImageResource(R.drawable.music_on);
        }
        else {
            musicButton.setImageResource(R.drawable.music_off);
        }
    }

    public interface Listener {
        void onProfilePageRequested();

        void onStartGameRequested() throws Exception;

        void onShowAchievementsRequested();

        void onShowLeaderboardRequested();

        void onShowSettingsRequested();

        void onSoundToggled();

        void onMusicToggled();
    }
}
