package shu.apps.eyespy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

//TODO: Rearrange the drawable folder to be more organised.

public class MainActivity extends FragmentActivity implements
        MainMenuFragment.Listener {

    private MainMenuFragment mMainMenuFragment;

    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleSignInClient = GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        mMainMenuFragment = new MainMenuFragment();

        mMainMenuFragment.setListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mMainMenuFragment).commit();
    }

    @Override
    public void onProfilePageRequested() {

    }

    @Override
    public void onStartGameRequested() {

    }

    @Override
    public void onShowAchievementsRequested() {

    }

    @Override
    public void onShowLeaderboardRequested() {

    }

    @Override
    public void onShowSettingsRequested() {

    }

    // Switch UI to the given fragment.
    private void switchToFragment(Fragment newFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .commit();
    }

    // Returns whether the user is currently signed into a Google account.
    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

}
