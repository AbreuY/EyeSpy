package shu.eyespy.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import shu.eyespy.R;

//TODO: Change to BaseFragment and don't allow back space.
public class ResultFragment extends Fragment {

    private View mView;
    private ImageView mImageView;
    private TextView mStatusTextView;

    private Bitmap bitmap;
    private String status;

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = RotateBitmap(bitmap, 90);

        updateUI();
    }

    public void setStatus(String status) {
        this.status = status;

        updateUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_result ,container, false);

        mImageView = mView.findViewById(R.id.result_image_taken);
        mStatusTextView = mView.findViewById(R.id.result_status_text_view);

        updateUI();

        return mView;
    }

    public void updateUI() {
        if (mView == null) {
            return;
        }

        mImageView.setImageBitmap(bitmap);
        mStatusTextView.setText(status);
    }
}
