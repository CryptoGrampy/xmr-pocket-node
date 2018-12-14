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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

public class ThemeActivity extends AppCompatPreferenceActivity {
    private static final String TAG = ThemeActivity.class.getSimpleName();
    private Context context = null;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ThemeActivity.MainPreferenceFragment()).commit();

        setContentView(R.layout.activity_theme);
        MainActivity.setThemeActivity(this);
        setTheme(MainActivity.getStyle(context));
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(context, MainActivity.getToolbarBg(context)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_theme);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(context, MainActivity.getToolbarBg(context)));
        setTheme(MainActivity.getStyle(context));
    }

}
