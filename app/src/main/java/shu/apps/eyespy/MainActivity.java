package shu.apps.eyespy;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import shu.apps.eyespy.fragments.CameraFragment;
import shu.apps.eyespy.fragments.ItemSelectFragment;
import shu.apps.eyespy.fragments.MainMenuFragment;
import shu.apps.eyespy.fragments.TrophiesFragment;
import shu.apps.eyespy.utilities.PackageManagerUtils;

//TODO: Rearrange the drawable folder to be more organised.

public class MainActivity extends FragmentActivity implements
        MainMenuFragment.Listener,
        ItemSelectFragment.ItemSelectedCallback,
        CameraFragment.Callback {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyDUojPDdBgIJv_b9r1tQPHFpk2IBo7fR64";
    private static final int MAX_DIMENSION = 1200;
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SIGN_IN = 9001;
    private static final int REQUEST_CODE_UNUSED = 9002;

    private MainMenuFragment mMainMenuFragment;
    private ItemSelectFragment mItemSelectFragment;
    private CameraFragment mCameraFragment;
    private TrophiesFragment mTrophiesFragment;

    private GoogleSignInClient mGoogleSignInClient;
    private PlayersClient mPlayersClient;
    private AchievementsClient mAchievementsClient;

    public static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getName())) {
                return true;
            }
        }
        return false;
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("Nothing found.");
        }
        return message.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);


        mMainMenuFragment = new MainMenuFragment();
        mCameraFragment = new CameraFragment();
        mTrophiesFragment = new TrophiesFragment();
        mItemSelectFragment = new ItemSelectFragment();

        mMainMenuFragment.setListener(this);
        mItemSelectFragment.setSelectedItemCallback(this);
        mCameraFragment.setCallback(this);

        setFragmentToContainer(mMainMenuFragment);
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
        //        mMainMenuFragment).commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // State of signed in user could change when changing from another app,
        // try and sign in again when the app resumes.
        signInSilently();
    }

    // Returns whether the user is currently signed into a Google account.
    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    private void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            onConnected(task.getResult());
                        } else {
                            final ApiException exception = (ApiException) task.getException();
                            if (exception.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
                                startSignInIntent();
                            }
                        }
                    }
                });
    }

    private void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException e) {
                String message = e.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.sign_in_other_error);
                }

                onDisconnected();

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
    }

    private void onConnected(GoogleSignInAccount account) {
        Log.d(TAG, "onConnected(): Connected to Google APIs.");

        //TODO: Should we store the player as a class? This may allow for anonymous users?
        mPlayersClient = Games.getPlayersClient(this, account);
        mAchievementsClient = Games.getAchievementsClient(this, account);

        mPlayersClient.getCurrentPlayer()
                .addOnCompleteListener(new OnCompleteListener<Player>() {
                    @Override
                    public void onComplete(@NonNull Task<Player> task) {
                        if (task.isSuccessful()) {
                            final String username = Objects.requireNonNull(task.getResult()).getDisplayName();
                            Log.d(TAG, "onConnected(): Username - " + username);
                            mMainMenuFragment.setUsername(username);
                        }
                    }
                });

        mAchievementsClient.load(false)
                .addOnCompleteListener(new OnCompleteListener<AnnotatedData<AchievementBuffer>>() {
                    @Override
                    public void onComplete(@NonNull Task<AnnotatedData<AchievementBuffer>> task) {
                        if (task.isSuccessful()) {
                            AchievementBuffer achievementsBuffer = task.getResult().get();
                            if (achievementsBuffer != null) {
                                Log.d(TAG, Integer.toString(achievementsBuffer.getCount()));
                                List<Achievement> achievements = new ArrayList<>();
                                for (int i = 0; i < achievementsBuffer.getCount(); i++) {
                                    achievements.add(achievementsBuffer.get(i));
                                }
                                mTrophiesFragment.setAchievements(achievements);
                            }
                        }
                    }
                });
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        mPlayersClient = null;
    }

    @Override
    public void onProfilePageRequested() {

    }

    @Override
    public void onStartGameRequested() {
        //TODO: Pull which of the 3 items we want the user to be able to choose from.

        setFragmentToContainer(mItemSelectFragment);
    }

    @Override
    public void onShowAchievementsRequested() {
        // If not signed in then try and sign in.
        if (!isSignedIn()) {
            startSignInIntent();
        }

        if (isSignedIn()) {
            setFragmentToContainer(mTrophiesFragment);
        }
    }

    @Override
    public void onShowLeaderboardRequested() {
        // If not signed in then try and sign in.
        if (!isSignedIn()) {
            startSignInIntent();
        }

        if (isSignedIn()) {
            // TODO: Show Leaderboard Fragment
        }
    }

    @Override
    public void onShowSettingsRequested() {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Log out successful.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(Item selectedItem) {
        mCameraFragment.setSelectedItem(selectedItem);
        setFragmentToContainer(mCameraFragment);
    }

    public void setFragmentToContainer(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final String fragmentTag = fragment.getClass().getSimpleName();

        if (isFragmentInBackstack(fragmentManager, fragmentTag)) {
            // Fragment exists, go back to that fragment
            //// you can also use POP_BACK_STACK_INCLUSIVE flag, depending on flow
            fragmentManager.popBackStackImmediate(fragmentTag, 0);
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            if (!(fragment instanceof MainMenuFragment)) {
                transaction.addToBackStack(fragmentTag);
            }
            transaction.commit();
        }

    }

    @Override
    public void onImageTaken(File file) {
        uploadImage(Uri.fromFile(file));
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                        );
                callCloudVision(bitmap);
                //mMainImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);
                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);
                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);
                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);
        Vision vision = builder.build();
        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
            // Add the image
            com.google.api.services.vision.v1.model.Image base64EncodedImage = new com.google.api.services.vision.v1.model.Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);
            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);

        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);

        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);

        Log.d(TAG, "created Cloud Vision request object, sending request");
        return annotateRequest;
    }

    private void callCloudVision(final Bitmap bitmap) {
        // TODO:: Make some sort of loading text.
        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap) {
        final int originalWidth = bitmap.getWidth();
        final int originalHeight = bitmap.getHeight();
        int resizedWidth = MainActivity.MAX_DIMENSION;
        int resizedHeight = MainActivity.MAX_DIMENSION;

        if (originalHeight > originalWidth) {
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;

        private Vision.Images.Annotate mRequest;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);
            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
                Log.d(TAG, result);
            }
        }
    }


}
