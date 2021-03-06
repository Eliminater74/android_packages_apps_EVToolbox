/*
 * Copyright (C) 2013 The Evervolv Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evervolv.toolbox.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.evervolv.toolbox.R;
import com.evervolv.toolbox.Toolbox;

public class StatusbarGeneral extends PreferenceFragment implements
        OnPreferenceChangeListener,
        Toolbox.DisabledListener {

    private static final String STATUSBAR_SIXBAR_SIGNAL = "pref_statusbar_sixbar_signal";
    private static final String STATUSBAR_BATTERY_STYLE = "pref_statusbar_batt_style";
    private static final String STATUSBAR_CLOCK_AM_PM_STYLE = "pref_statusbar_clock_am_pm_style";

    private static final int BATT_STYLE_DEFAULT = 1;
    private static final int AM_PM_STYLE_DEFAULT = 2;

    private ContentResolver mCr;
    private PreferenceScreen mPrefSet;

    private CheckBoxPreference mUseSixbaricons;
    private ListPreference mBattStyle;
    private ListPreference mClockAmPmStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar_general);

        mPrefSet = getPreferenceScreen();

        mCr = getActivity().getContentResolver();

        /* Sixbar Signal Icons */
        mUseSixbaricons = (CheckBoxPreference) mPrefSet.findPreference(
                STATUSBAR_SIXBAR_SIGNAL);
        mUseSixbaricons.setChecked(Settings.System.getInt(mCr,
                Settings.System.STATUSBAR_6BAR_SIGNAL, 1) == 1);

        /* Remove mUseSixbaricons on devices without mobile radios */
        if (!getResources().getBoolean(R.bool.config_has_mobile_radio)) {
            mPrefSet.removePreference(mUseSixbaricons);

        }

        /* Battery Icon Style */
        mBattStyle = (ListPreference) mPrefSet.findPreference(STATUSBAR_BATTERY_STYLE);
        mBattStyle.setValue(Integer.toString(Settings.System.getInt(mCr,
                Settings.System.STATUSBAR_BATT_STYLE, BATT_STYLE_DEFAULT)));
        mBattStyle.setOnPreferenceChangeListener(this);

        /* Clock AM/PM Style */
        mClockAmPmStyle = (ListPreference) mPrefSet.findPreference(STATUSBAR_CLOCK_AM_PM_STYLE);
        mClockAmPmStyle.setValue(Integer.toString(Settings.System.getInt(mCr,
                Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE, AM_PM_STYLE_DEFAULT)));
        mClockAmPmStyle.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPrefSet.setEnabled(Toolbox.isEnabled(getActivity()));
        ((Toolbox) getActivity()).registerCallback(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((Toolbox) getActivity()).unRegisterCallback(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBattStyle) {
            Settings.System.putInt(mCr, Settings.System.STATUSBAR_BATT_STYLE,
                    Integer.valueOf((String) newValue));
            return true;
        }
        if (preference == mClockAmPmStyle) {
            Settings.System.putInt(mCr, Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE,
                    Integer.valueOf((String) newValue));
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mUseSixbaricons) {
            value = mUseSixbaricons.isChecked();
            Settings.System.putInt(mCr, Settings.System.STATUSBAR_6BAR_SIGNAL,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onToolboxDisabled(boolean enabled) {
        mPrefSet.setEnabled(enabled);
    }

}
