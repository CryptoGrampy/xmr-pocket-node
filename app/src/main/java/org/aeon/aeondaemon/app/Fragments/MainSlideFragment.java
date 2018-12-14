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
package org.aeon.aeondaemon.app.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.aeon.aeondaemon.app.MainActivity;
import org.aeon.aeondaemon.app.R;
import org.aeon.aeondaemon.app.model.CollectPreferences;
import org.aeon.aeondaemon.app.model.Launcher;
import org.aeon.aeondaemon.app.model.SynchronizeThread;

import java.io.File;

public class MainSlideFragment extends Fragment {
    private static final String TAG = MainSlideFragment.class.getSimpleName();
    private static long RefreshInterval = 1000;
    public static String execError = null;
    private ViewGroup rootView;
    private Context context = null;
    private static boolean hasCriticalError = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.main_fragment, container, false);
        context = getActivity();

        final Handler handler = new Handler();

        Runnable r = new Runnable()  {
            @Override
            public void run() {
                doUpdate();
                handler.postDelayed( this, RefreshInterval );
            }
        };
        handler.postDelayed(r,RefreshInterval);

        TextView v = (TextView) rootView.findViewById(R.id.sync_status);
        v.setText(getActivity().getString(R.string.sync_starting));

        rootView.setBackground(ContextCompat.getDrawable(context,MainActivity.getBg(context)));

        return rootView;
    }

    private void doUpdate() {
        boolean hasFocus = MainActivity.getmViewPager().getCurrentItem() == MainActivity.FRAGMENT_MAIN;
        if (hasFocus) {
            if (hasCriticalError) {
                Log.d(TAG,"hasCriticalError");
                return;
            }
            if (execError != null) {
                if(!((Activity) context).isFinishing()) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("wownerod")
                            .setMessage(execError)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                execError = null;
                hasCriticalError = true;
            }

            Launcher launcher = SynchronizeThread.getLauncher();
            if (launcher == null) return;

            if (launcher.isStarting()) {
                TextView v = (TextView) rootView.findViewById(R.id.sync_status);
                v.setText(context.getString(R.string.sync_starting));
            } else if (launcher.isAlive()) {
                TextView v = (TextView) rootView.findViewById(R.id.heightValue);
                v.setText(launcher.getHeight());

                v = (TextView) rootView.findViewById(R.id.heightTarget);
                v.setText(launcher.getTarget());

                v = (TextView) rootView.findViewById(R.id.compiledMsgAeonVersion);
                if (launcher.getVersion() != null) v.setText(launcher.getVersion());

                v = (TextView) rootView.findViewById(R.id.peers);
                if (launcher.getPeers() != null)
                    v.setText(launcher.getPeers() + " " + context.getString(R.string.msg_peers_connected));

                v = (TextView) rootView.findViewById(R.id.downloading);
                if (launcher.getDownloading() != null)
                    v.setText(context.getString(R.string.download_at) + " " + launcher.getDownloading() + " kB/s");

                v = (TextView) rootView.findViewById(R.id.disk);
                String s = String.format("%.1f", getUsedSpace());
                v.setText(s + " " + context.getString(R.string.disk_used));

                v = (TextView) rootView.findViewById(R.id.sync_status);
                v.setText("");
            }
        }
    }

    /**
     * Get availabe free space on the disk
     *
     * @return percentage of free space.
     */
    private float getUsedSpace() {
        File f = new File(CollectPreferences.collectedPreferences.isUseSDCard() ? CollectPreferences.collectedPreferences.getSdCardPath() : MainActivity.BINARY_PATH);
        return f.getFreeSpace() / 1024.0f / 1024.0f / 1024.0f;
    }

    @Override
    public void onResume(){
        super.onResume();

        rootView.setBackground(ContextCompat.getDrawable(context,MainActivity.getBg(context)));
    }

    @Override
    public void onAttach(Context _context) {
        super.onAttach(_context);
        context = _context;
    }

    public static void setHasCriticalError(boolean _hasCriticalError) {
        hasCriticalError = _hasCriticalError;
    }
}
