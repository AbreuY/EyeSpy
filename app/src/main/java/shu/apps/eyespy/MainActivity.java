package shu.apps.eyespy;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import shu.apps.eyespy.fragments.CameraFragment;
import shu.apps.eyespy.fragments.ItemSelectFragment;
import shu.apps.eyespy.fragments.MainMenuFragment;
import shu.apps.eyespy.fragments.TrophiesFragment;

//TODO: Rearrange the drawable folder to be more organised.

public class MainActivity extends FragmentActivity implements
        MainMenuFragment.Listener,
        ItemSelectFragment.ItemSelectedCallback {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleSignInClient = GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        mMainMenuFragment = new MainMenuFragment();
        mCameraFragment = new CameraFragment();
        mTrophiesFragment = new TrophiesFragment();
        mItemSelectFragment = new ItemSelectFragment();

        mMainMenuFragment.setListener(this);
        mItemSelectFragment.setSelectedItemCallback(this);

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
                            Log.d(TAG, "signInSilently(): Success.");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): Failure.", task.getException());
                            onDisconnected();
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
            //TODO: Change this to use custom UI.
            /* mAchievementsClient.getAchievementsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, REQUEST_CODE_UNUSED);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //handleException(e, getString(R.string.achievements_exception));
                        }
                    });
                    */
            // TODO: Show Achievements Fragment
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

    }

    @Override
    public void onItemSelected(Item selectedItem) {
        mCameraFragment.setSelectedItem(selectedItem);
        setFragmentToContainer(mCameraFragment);
    }

    public void setFragmentToContainer(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final String fragmentTag  = fragment.getClass().getSimpleName();

        if (isFragmentInBackstack(fragmentManager,fragmentTag)) {
            // Fragment exists, go back to that fragment
            //// you can also use POP_BACK_STACK_INCLUSIVE flag, depending on flow
            fragmentManager.popBackStackImmediate(fragmentTag, 0);
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            if (fragment instanceof MainMenuFragment) {
                //Exit app on back press
            } else {
                transaction.addToBackStack(fragmentTag);
            }
            transaction.commit();
        }

    }

    public static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getName())) {
                return true;
            }
        }
        return false;
    }
}
