package shu.eyespy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import shu.eyespy.Item;
import shu.eyespy.R;

public class ItemSelectFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private ArrayList<Item> items;

    private TextView mLevelSelectEasyTextView;
    private TextView mLevelSelectMediumTextView;
    private TextView mLevelSelectHardTextView;

    private ItemSelectedCallback itemSelectedListener;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.level_select_easy_button:
                itemSelectedListener.onItemSelected(items.get(0));
                break;
            case R.id.level_select_medium_button:
                itemSelectedListener.onItemSelected(items.get(1));
                break;
            case R.id.level_select_hard_button:
                itemSelectedListener.onItemSelected(items.get(2));
                break;
        }
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;

        if (mView != null) {
            updateUI();
        }
    }

    public void updateUI() {
        mLevelSelectEasyTextView.setText(items.get(0).getName());
        mLevelSelectMediumTextView.setText(items.get(1).getName());
        mLevelSelectHardTextView.setText(items.get(2).getName());
    }


    public void setSelectedItemCallback(ItemSelectedCallback itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_level_selection, container, false);

        final int[] clickableIds = new int[]{
                R.id.level_select_easy_button,
                R.id.level_select_medium_button,
                R.id.level_select_hard_button,
        };

        mLevelSelectEasyTextView = mView.findViewById(R.id.level_select_easy_text_view);
        mLevelSelectMediumTextView = mView.findViewById(R.id.level_select_medium_text_view);
        mLevelSelectHardTextView = mView.findViewById(R.id.level_select_hard_text_view);

        for (int clickableId : clickableIds) {
            mView.findViewById(clickableId).setOnClickListener(this);
        }

        updateUI();

        return mView;
    }

    public interface ItemSelectedCallback {
        void onItemSelected(Item selectedItem);
    }
}
