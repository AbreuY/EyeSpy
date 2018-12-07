package shu.apps.eyespy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import shu.apps.eyespy.DynamicTrophies;
import shu.apps.eyespy.R;

public class TrophiesFragment extends Fragment {

    private static final String TAG = TrophiesFragment.class.getSimpleName();

    private GridView mTrophiesGridView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trophies, container, false);

        mTrophiesGridView = view.findViewById(R.id.trophies_grid_view);
        mTrophiesGridView.setVerticalScrollBarEnabled(false);
        mTrophiesGridView.setAdapter(new DynamicTrophies(getActivity()));

        //TODO: Create a listener to handle the button click in main activity.
        mTrophiesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), String.valueOf(position),
                        Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
