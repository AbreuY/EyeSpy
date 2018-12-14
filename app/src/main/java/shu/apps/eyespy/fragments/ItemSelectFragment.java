package shu.apps.eyespy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shu.apps.eyespy.Item;
import shu.apps.eyespy.R;

public class ItemSelectFragment extends Fragment implements View.OnClickListener {

    //TODO: This will change when we are passing them in customly, will hold the list within here for the items.
    //TODO: Will this be in the mainactivity and the items passed into a setter? Best game practice?
    private Item[] items = new Item[] {
            new Item("Banana", Item.ItemDifficulty.EASY),
            new Item("Dog", Item.ItemDifficulty.MEDIUM),
            new Item("Castle", Item.ItemDifficulty.HARD)
    };

    private ItemSelectedCallback itemSelectedListener;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.level_select_easy_button:
                itemSelectedListener.onItemSelected(items[0]);
                break;
            case R.id.level_select_medium_button:
                itemSelectedListener.onItemSelected(items[1]);
                break;
            case R.id.level_select_hard_button:
                itemSelectedListener.onItemSelected(items[2]);
                break;
        }
    }

    public void setSelectedItemCallback(ItemSelectedCallback itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_selection, container, false);

        final int[] clickableIds = new int[]{
                R.id.level_select_easy_button,
                R.id.level_select_medium_button,
                R.id.level_select_hard_button,
        };

        for (int clickableId : clickableIds) {
            view.findViewById(clickableId).setOnClickListener(this);
        }

        return view;
    }

    public interface ItemSelectedCallback {
        void onItemSelected(Item selectedItem);
    }
}
