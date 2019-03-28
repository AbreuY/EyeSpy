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
import java.util.Locale;

import shu.eyespy.Item;
import shu.eyespy.OnHomeButtonListener;
import shu.eyespy.R;

public class ItemSelectFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private ArrayList<Item> items;

    private TextView mLevelSelectEasyTextView;
    private TextView mLevelSelectMediumTextView;
    private TextView mLevelSelectHardTextView;

    private ItemSelectedCallback itemSelectedListener;
    private OnHomeButtonListener mListener = null;


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
            case R.id.level_select_back_button:
                mListener.onHomePressed();
        }
    }

    public void setOnHomeButtonListener(OnHomeButtonListener listener) {
        mListener = listener;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;

        if (mView != null) {
            updateUI();
        }
    }

    public void updateUI() {
        final String localeInfo = Locale.getDefault().getCountry();

        mLevelSelectEasyTextView.setText(items.get(0).getName(localeInfo));
        mLevelSelectMediumTextView.setText(items.get(1).getName(localeInfo));
        mLevelSelectHardTextView.setText(items.get(2).getName(localeInfo));
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

        mView.findViewById(R.id.level_select_back_button).setOnClickListener(this);
        updateUI();

        return mView;
    }

    public interface ItemSelectedCallback {
        void onItemSelected(Item selectedItem);
    }
}
