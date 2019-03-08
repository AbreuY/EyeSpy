package shu.eyespy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.achievement.Achievement;

import java.util.ArrayList;
import java.util.List;

import shu.eyespy.R;


public class TrophiesFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = TrophiesFragment.class.getSimpleName();

    private Toast mAchievementToast;
    private View mView;
    private GridView mTrophiesGridView;
    private Trophies mTrophies;

    public TrophiesFragment() {
        this.mTrophies = new Trophies();
    }

    public void setAchievements(List<Achievement> achievements) {
        this.mTrophies.setAchievements(achievements);

        updateUI();
    }

    private void updateUI() {
        if (mView == null)
            return;

        mTrophiesGridView.setAdapter(mTrophies);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_trophies, container, false);

        mTrophies.setContext(getActivity());

        mTrophiesGridView = mView.findViewById(R.id.trophies_grid_view);
        mTrophiesGridView.setVerticalScrollBarEnabled(false);

        //TODO: Create a listener to handle the button click in main activity.
        mTrophiesGridView.setOnItemClickListener(this);

        updateUI();
        return mView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.achievement_toast, (ViewGroup) view.findViewById(R.id.achievement_toast_root));

        String title = "???", description = "???";
        Achievement achievement = mTrophies.getAchievement(position);
        if (achievement.getState() == Achievement.STATE_REVEALED
                || achievement.getState() == Achievement.STATE_UNLOCKED) {
            title = achievement.getName();
            description = achievement.getDescription();
        }

        ImageView trophy = layout.findViewById(R.id.achievement_toast_trophy);
        if (/*achievement.getState() == Achievement.STATE_REVEALED
                    || */achievement.getState() == Achievement.STATE_HIDDEN) {
            trophy.setImageResource(R.drawable.trophy_locked);
        } else {
            trophy.setImageResource(R.drawable.trophy_unlocked);
        }

        TextView achievementTitleTextView = layout.findViewById(R.id.achievement_toast_title);
        TextView achievementDescriptionTextView = layout.findViewById(R.id.achievement_toast_description);

        achievementTitleTextView.setText(title);
        achievementDescriptionTextView.setText(description);

        mAchievementToast= new Toast(getContext());
        mAchievementToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 80);
        mAchievementToast.setDuration(Toast.LENGTH_LONG);
        mAchievementToast.setView(layout);
        mAchievementToast.show();
    }

    @Override
    public boolean onBackPressed() {
        if (mAchievementToast != null) {
            mAchievementToast.cancel();
            mAchievementToast = null;
            return true;
        }
        return false;
    }


    class Trophies extends BaseAdapter {

        Achievement getAchievement(int position) {
            return mAchievements.get(position);
        }

        private Context mContext;
        private List<Achievement> mAchievements;

        public Trophies() {
            mAchievements = new ArrayList<>();
        }

        void setContext(Context context) {
            this.mContext = context;
        }

        public int getCount() {
            return mAchievements.size();
        }

        public Object getItem(int position) {
            return null;
        }

        //TODO: Look into this as it does not read right.
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPaddingRelative(50, 50, 50, 50);
                imageView.setBackgroundResource(R.drawable.curved_button_template);
            } else {
                imageView = (ImageView) convertView;
            }

            Achievement achievement = mAchievements.get(position);
            if (/*achievement.getState() == Achievement.STATE_REVEALED
                    || */achievement.getState() == Achievement.STATE_HIDDEN) {
                imageView.setImageResource(R.drawable.trophy_locked);
            } else {
                imageView.setImageResource(R.drawable.trophy_unlocked);
            }
            return imageView;
        }

        void setAchievements(List<Achievement> achievements) {
            mAchievements = achievements;
        }

    }

}
