/* Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shu.apps.eyespy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MainMenuFragment extends Fragment implements OnClickListener {

    private static final String TAG = MainMenuFragment.class.getSimpleName();

    interface Listener {
        void onProfilePageRequested();

        void onStartGameRequested();

        void onShowAchievementsRequested();

        void onShowLeaderboardRequested();

        void onShowSettingsRequested();
    }

    private Listener mListener = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        final int[] clickableIds = new int[]{
                R.id.main_menu_profile_button,
                R.id.main_menu_play_button,
                R.id.main_menu_trophies_button,
                R.id.main_menu_rankings_button,
                R.id.main_menu_options_button
        };

        for (int clickableId : clickableIds) {
            view.findViewById(clickableId).setOnClickListener(this);
        }

        return view;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.getId());
        switch (view.getId()) {
            case R.id.main_menu_profile_button:
                mListener.onProfilePageRequested();
                break;
            case R.id.main_menu_play_button:
                mListener.onStartGameRequested();
                break;
            case R.id.main_menu_trophies_button:
                mListener.onShowAchievementsRequested();
                break;
            case R.id.main_menu_rankings_button:
                mListener.onShowLeaderboardRequested();
                break;
            case R.id.main_menu_options_button:
                mListener.onShowSettingsRequested();
        }
    }
}
