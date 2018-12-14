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
package org.aeon.aeondaemon.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.aeon.aeondaemon.app.Fragments.MainSlideFragment;

public class SynchronizeThread implements Runnable  {
    private static final String TAG = SynchronizeThread.class.getSimpleName();
    private static Launcher launcher = null;
    private Context context;
    private static long RefreshInterval = 1000;
    private int counter = 0;
    private static int SendSyncCmd = 10;

    public SynchronizeThread(Context _context) {
        this.context = _context;
    }

    @Override
    public void run() {
        while (true) {
            if (launcher == null) {
                launcher = new Launcher();
                updatePreferences();
            }
            if (launcher.isStopped()) {
                // Restart the background process
                updatePreferences();        // properties may have been changed in the settings.
                String status = launcher.start(CollectPreferences.collectedPreferences);
                if (status != null) {
                    launcher.updateStatus();
                    String msg = "";
                    if (launcher.getLogs().length() > 0) msg = launcher.getLogs();
                    else msg = "wownerod process failed to start. err=" + status;
                    MainSlideFragment.execError = msg;
                }
            } else if (launcher.isAlive()) {
                if (counter >= SendSyncCmd) {
                    launcher.getSyncInfo();
                    counter = 0;
                }
                launcher.updateStatus();
                //Log.e(TAG,"launcher.getSyncInfo");
            }

            try {
                Thread.sleep(RefreshInterval);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
            counter ++;
        }
    }

    private void updatePreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        CollectPreferences.collect(preferences);

        // If using internal storage, set the path for it.
        if (CollectPreferences.collectedPreferences.isUseSDCard()) {
            CollectPreferences.collectedPreferences.setDataDir(CollectPreferences.collectedPreferences.getSdCardPath());
        } else if (CollectPreferences.collectedPreferences.isUseCustomStorage()) {
            CollectPreferences.collectedPreferences.setDataDir(CollectPreferences.collectedPreferences.getCustomStoragePath());
        } else {
            String storagePath = context.getFilesDir().getPath();
            CollectPreferences.collectedPreferences.setDataDir(storagePath);
        }
    }

    public static Launcher getLauncher() {
        return launcher;
    }
}
