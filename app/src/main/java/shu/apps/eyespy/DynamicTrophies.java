package shu.apps.eyespy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class DynamicTrophies extends BaseAdapter {
    private Context mContext;

    public DynamicTrophies(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

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
            imageView.setPaddingRelative(50,50,50,50);

            imageView.setBackgroundResource(R.drawable.curved_button_template);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow,
            R.drawable.trophy_shadow, R.drawable.trophy_shadow
    };
}