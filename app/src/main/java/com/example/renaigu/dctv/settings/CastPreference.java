package com.example.renaigu.dctv.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

import com.example.renaigu.dctv.R;
import com.example.renaigu.dctv.utils.Utils;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;

public class CastPreference extends PreferenceActivity {

    private VideoCastManager mCastManager;

    @SuppressLint("StringFormatInvalid")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.application_preference);
        mCastManager = VideoCastManager.getInstance();

        EditTextPreference versionPref = (EditTextPreference) findPreference("app_version");
        versionPref.setTitle(getString(R.string.version, Utils.getAppVersionName(this),
                getString(R.string.ccl_version)));
    }

    @Override
    protected void onResume() {
        if (null != mCastManager) {
            mCastManager.incrementUiCounter();
            mCastManager.updateCaptionSummary("caption", getPreferenceScreen());
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (null != mCastManager) {
            mCastManager.decrementUiCounter();
        }
        super.onPause();
    }

}
