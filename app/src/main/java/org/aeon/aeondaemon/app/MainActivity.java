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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Build;

import org.aeon.aeondaemon.app.Fragments.LogSlideFragment;
import org.aeon.aeondaemon.app.Fragments.MainSlideFragment;
import org.aeon.aeondaemon.app.model.SynchronizeThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String BINARY_PATH = null;
    public static String PACKAGE_NAME;
    public static int FRAGMENT_MAIN=0;
    public static int FRAGMENT_LOG=1;
    private static AppCompatPreferenceActivity logActivity;
    private static AppCompatPreferenceActivity aboutActivity;
    private static AppCompatPreferenceActivity themeActivity;
    private static boolean initDone = false;
    private static ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SynchronizeThread synchronizeThread = null;
    private static Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        context = getApplicationContext();

        // if not initialized - because onCreate is called on screen rotation.
        if (!initDone) copyBinaryFile();

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(context, getToolbarBg(context)));

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Create the background synchrpnioation thread
        if (synchronizeThread == null) {
            synchronizeThread = new SynchronizeThread(context);
            Thread t = new Thread(synchronizeThread);
            t.start();
        }

        initDone = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(context, getToolbarBg(context)));
        setTheme(MainActivity.getStyle(context));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // launch settings activity
            startActivity(new Intent(MainActivity.this, SettingsPrefActivity.class));
            return true;
        }
        if (id == R.id.action_theme) {
            // launch settings activity
            startActivity(new Intent(MainActivity.this, ThemeActivity.class));
            return true;
        }
        if (id == R.id.action_about) {
            // launch about activity
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Copy the aeond binary file to a location where wa have execute rights
     */
    private void copyBinaryFile() {
        Resources res = getResources();
        //Log.d(TAG, " " + is64bitsProcessor());

        InputStream in_s = res.openRawResource(is64bitsProcessor() ? R.raw.wownerod64 : R.raw.wownerod32);
        try {
            // read wownerod binary file from the ressource raw folder
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            String pathName = context.getApplicationInfo().dataDir + "/lib";

            BINARY_PATH = context.getCacheDir().getPath() + "/../wownerod";

            // write the file to an android executable location
            OutputStream outputStream = new FileOutputStream(BINARY_PATH);
            outputStream.write(b);
            outputStream.flush();
            outputStream.close();

            // make the file executable
            File f = new File(BINARY_PATH);
            f.setExecutable(true);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }
            builder.setTitle("Copy binary file")
                    .setMessage(e.getMessage())
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }


    public static void setAboutActivity(AppCompatPreferenceActivity _aboutActivity) {
        aboutActivity = _aboutActivity;
    }

    private boolean is64bitsProcessor() {
        String supported[] = Build.SUPPORTED_ABIS;
        for (String s : supported) {
            if (s.equals("arm64-v8a")) return true;
        }
        return false;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == FRAGMENT_MAIN) return new MainSlideFragment();
            else if (position == FRAGMENT_LOG) return new LogSlideFragment();
            else return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static ViewPager getmViewPager() {
        return mViewPager;
    }

    public static void setThemeActivity(AppCompatPreferenceActivity themeActivity) {
        MainActivity.themeActivity = themeActivity;
    }

    public static int getStyle(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = preferences.getString("theme", null);
        if (theme == null)
            return R.style.PreferencesThemeWownero4;
        else if (theme.equals("PreferencesThemeWownero1"))
            return R.style.PreferencesThemeWownero1;
        else if (theme.equals("PreferencesThemeWownero2"))
            return R.style.PreferencesThemeWownero2;
        else if (theme.equals("PreferencesThemeWownero3"))
            return R.style.PreferencesThemeWownero3;
        else
            return R.style.PreferencesThemeWownero4;
    }

    public static int getToolbarBg(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = preferences.getString("theme", null);
        if (theme == null)
            return R.drawable.wsolidcolor;
        else if (theme.equals("PreferencesThemeWownero1"))
            return R.drawable.wsolidcolor;
        else if (theme.equals("PreferencesThemeWownero2"))
            return R.drawable.wsolidcolor;
        else if (theme.equals("PreferencesThemeWownero3"))
            return R.drawable.wsolidcolor;
        else
            return R.drawable.wsolidcolor;
    }

    public static int getBg(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = preferences.getString("theme", null);
        if (theme == null)
            return R.drawable.w001;
        else if (theme.equals("PreferencesThemeWownero1"))
            return R.drawable.wsolidcolor;
        else if (theme.equals("PreferencesThemeWownero2"))
            return R.drawable.wcoin;
        else if (theme.equals("PreferencesThemeWownero3"))
            return R.drawable.wowario;
        else
            return R.drawable.w001;
    }

    public static Context getContext() {
        return context;
    }
}