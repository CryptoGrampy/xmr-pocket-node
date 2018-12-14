/**
 * Copyright (c) 2018 enerc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aeon.aeondaemon.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import org.aeon.aeondaemon.app.Fragments.MainSlideFragment;
import org.aeon.aeondaemon.app.model.CollectPreferences;
import org.aeon.aeondaemon.app.model.Launcher;
import org.aeon.aeondaemon.app.model.SynchronizeThread;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsPrefActivity.class.getSimpleName();
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private Context context = null;
    private Activity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getApplicationContext();
        activity = this;

        // load settings fragment
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)  {
                        Launcher launcher = SynchronizeThread.getLauncher();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        // If SD card enabled set a preference if empty
                        String path = preferences.getString("sd_storage","");
                        boolean useSD = preferences.getBoolean("use_sd_card",false);
                        // Card has been inserted and "Use SD card" checked
                        if (useSD && path.equals("")) {
                            SharedPreferences.Editor editor = preferences.edit();
                            if (CollectPreferences.getExternalStoragePath() != null)
                                editor.putString("sd_storage",CollectPreferences.getExternalStoragePath());
                            else
                                editor.putBoolean("use_sd_card",false);
                            editor.commit();

                            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

                        }

                        // If custom location set a preference if empty
                        path = preferences.getString("sd_custom_storage","");
                        boolean useCustom = preferences.getBoolean("use_custom_storage",false);
                        // Card has been inserted and "Use SD card" checked
                        if (useCustom && path.equals("")) {

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("sd_custom_storage",CollectPreferences.getCustomPath());
                            editor.commit();

                            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

                        }

                        // Android 6+
                        if (useCustom || useSD) {
                            boolean hasPermission = (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                            if (!hasPermission) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        1);
                            }
                        }

                        if (useSD && useCustom) {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(SettingsPrefActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(SettingsPrefActivity.this);
                            }
                            builder.setTitle(R.string.sd_custom_error)
                                    .setMessage(R.string.sd_custom_error_msg)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("use_custom_storage",false);
                            editor.commit();

                            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
                        }
                        MainSlideFragment.setHasCriticalError(false);
                        if (launcher != null) {
                            Log.e(TAG, "Stop Wownero daemon");
                            launcher.exit();
                        }
                    }
                };
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.registerOnSharedPreferenceChangeListener(listener);

        String sdLocation = CollectPreferences.getExternalStoragePath();
        // if the SD card has been removed.
        if (sdLocation == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("use_sd_card",false);
            editor.commit();
        }
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(context, MainActivity.getToolbarBg(context)));
        setTheme(MainActivity.getStyle(getApplicationContext()));
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.wsolidcolor));
        setTheme(R.style.PreferencesThemeWownero1);
    }

}