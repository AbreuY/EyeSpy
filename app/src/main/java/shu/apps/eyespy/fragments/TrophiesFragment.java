package shu.apps.eyespy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.games.achievement.Achievement;

import java.util.List;

import shu.apps.eyespy.R;

public class TrophiesFragment extends Fragment {

    private static final String TAG = TrophiesFragment.class.getSimpleName();
    private GridView mTrophiesGridView;
    private Trophies mTrophies;

    public TrophiesFragment() {
        this.mTrophies = new Trophies();
    }

    public void setAchievements(List<Achievement> achievements) {
        this.mTrophies.setAchievements(achievements);

        if (mTrophiesGridView != null) {
            updateUI();
        }
    }

    private void updateUI() {
        mTrophiesGridView.setAdapter(mTrophies);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trophies, container, false);

        mTrophies.setContext(getActivity());

        mTrophiesGridView = view.findViewById(R.id.trophies_grid_view);
        mTrophiesGridView.setVerticalScrollBarEnabled(false);
        updateUI();

        //TODO: Create a listener to handle the button click in main activity.
        mTrophiesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), mTrophies.getAchievement(position).getDescription(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    class Trophies extends BaseAdapter {

        Achievement getAchievement(int position) {
            return mAchievements.get(position);
        }

        private Context mContext;
        private List<Achievement> mAchievements;

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
            if (achievement.getState() == Achievement.STATE_REVEALED
                    || achievement.getState() == Achievement.STATE_HIDDEN) {
                imageView.setImageResource(R.drawable.trophy_shadow);
            } else {
                imageView.setImageURI(achievement.getUnlockedImageUri());
            }
            return imageView;
        }

        void setAchievements(List<Achievement> achievements) {
            mAchievements = achievements;
        }

    }

}
