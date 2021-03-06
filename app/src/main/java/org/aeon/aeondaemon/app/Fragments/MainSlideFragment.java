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

import static org.aeon.aeondaemon.app.model.CollectPreferences.collectedPreferences;
import static java.lang.Integer.parseInt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.format.Formatter;
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
    private static SharedPreferences sharedPreferences;
    private static SwitchCompat nodeSwitch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.main_fragment, container, false);
        context = getActivity();
        setDisconnectedValues();

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
        v.setText(getActivity().getString(R.string.daemon_not_running));

        /**
         * On init, set switch to saved setting or use default
         *
         * on update, check node setting, update node status, update saved setting
         *
         */
        nodeSwitch = (SwitchCompat) rootView.findViewById(R.id.enable_node);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        initEnableNode();

        return rootView;
    }

    /**
     * Sets the enable_node Switch with the last saved state of the button
     * defaults to false
     * and actually enables the node if previous state was true
     */
    private void initEnableNode() {
        Boolean t = sharedPreferences.getBoolean("enable_node", false);
        SwitchCompat nodeSwitch = (SwitchCompat) rootView.findViewById(R.id.enable_node);
        nodeSwitch.setChecked(t.booleanValue());
        collectedPreferences.setEnableNode(t.booleanValue());
    }

    /**
     * Saves the toggle state of the button to be loaded on app restart
     * and actually turns on the node
     */
    private void updateEnableNode() {
        collectedPreferences.setEnableNode(nodeSwitch.isChecked());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("enable_node", nodeSwitch.isChecked());
        editor.commit();
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
                    builder.setTitle("monerod")
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

            updateEnableNode();


            Launcher launcher = SynchronizeThread.getLauncher();
            
            if (launcher == null) {
                TextView v = (TextView) rootView.findViewById(R.id.sync_status);
                v.setText(R.string.daemon_not_running);
            };

            if (launcher.isStarting()) {
                TextView v = (TextView) rootView.findViewById(R.id.sync_status);
                v.setText(context.getString(R.string.sync_starting));
            } else if (launcher.isAlive()) {
                TextView v = (TextView) rootView.findViewById(R.id.heightValue);
                String height = launcher.getHeight();
                if (height != null) {
                    v.setText("Height: " + height);
                }

                v = (TextView) rootView.findViewById(R.id.heightTarget);
                String targetHeight = launcher.getTarget();

                if (targetHeight != null) {
                    v.setText("Target Height: " + targetHeight);
                }

                v = (TextView) rootView.findViewById(R.id.syncPercentage);
                if (launcher.getSyncPercentage() != null) {
                    v.setText("Sync Progress: " + launcher.getSyncPercentage() + "%");
                }

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
                v.setText(R.string.daemon_running);

                v = (TextView) rootView.findViewById(R.id.local_ip_address);
                int port = collectedPreferences.getRpcBindPort();
                v.setText("Node Address: "+getLocalIpAddress()+(port > 0 ? (":"+port) : ":18081"));

            } else {
                // Unset all Main page values by default
                setDisconnectedValues();
            }
        }
    }

    private void setDisconnectedValues() {
        TextView v = (TextView) rootView.findViewById(R.id.heightValue);
        v.setText("");

        v = (TextView) rootView.findViewById(R.id.heightTarget);
        v.setText("");

        v = (TextView) rootView.findViewById(R.id.syncPercentage);
        v.setText("");

        v = (TextView) rootView.findViewById(R.id.compiledMsgAeonVersion);
        v.setText("");

        v = (TextView) rootView.findViewById(R.id.peers);
        v.setText("");

        v = (TextView) rootView.findViewById(R.id.downloading);
        v.setText("");

        v = (TextView) rootView.findViewById(R.id.disk);
        v.setText("");

        v = (TextView) rootView.findViewById(R.id.sync_status);
        v.setText(R.string.daemon_not_running);

        v = (TextView) rootView.findViewById(R.id.local_ip_address);
        v.setText("");
    }

    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ipAddress;
    }

    /**
     * Get availabe free space on the disk
     *
     * @return percentage of free space.
     */
    private float getUsedSpace() {
        File f = new File(collectedPreferences.isUseSDCard() ? collectedPreferences.getSdCardPath() : MainActivity.BINARY_PATH);
        return f.getFreeSpace() / 1024.0f / 1024.0f / 1024.0f;
    }

    @Override
    public void onResume(){
        super.onResume();
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
