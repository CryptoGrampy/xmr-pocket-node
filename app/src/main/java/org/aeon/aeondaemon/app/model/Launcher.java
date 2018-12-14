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

import android.util.Log;

import org.aeon.aeondaemon.app.Fragments.MainSlideFragment;
import org.aeon.aeondaemon.app.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class Launcher {
    private static final String TAG = Launcher.class.getSimpleName();
    private static int MAX_LOG_SIZE = 30000;
    private enum ProcessState { STOPPED, STARTING, RUNNING, STOPPING };

    private BufferedReader reader=null;
    private BufferedWriter writer=null;
    private String version=null;
    private StringBuffer logs=null;
    private String height;
    private String target;
    private String peers;
    private String downloading;
    private Process process=null;
    private ProcessState processState = ProcessState.STOPPED;

    public String start(Settings pref)  {
        if (!checkSDCard(pref)) {
            return "Fail to create "+pref.getSdCardPath();
        }
        MainSlideFragment.setHasCriticalError(false);
        try  {
            // reset log buffer
            if (logs != null) logs.delete(0,logs.length());

            File f = new File(MainActivity.BINARY_PATH);

            String env = getEnv(pref);
            Log.e(TAG , env);

            // Executes the command.
            process = Runtime.getRuntime().exec(MainActivity.BINARY_PATH+" "+env);

            try {
                Field field = process.getClass().getDeclaredField("pid");
                field.setAccessible(true);
            } catch (Throwable e) {
                Log.e(TAG,e.getMessage());
            }

            // maps stdout
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // maps stdin
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
            return e.getMessage();
        }
        processState = ProcessState.STARTING;

        return null;
    }

    /**
     * Collect the logs, get the version string and get current sync status
     *
     * Non blocking I/O
     */
    public void updateStatus() {
        try {
            //Log.d(TAG,"updateStatus "+reader.ready());
            if (reader != null && reader.ready()) {
                char[] buffer = new char[16384];
                if (logs == null) logs = new StringBuffer();

                while (reader.ready()) {
                    int read = reader.read(buffer);
                    if (read > 0) {
                        logs.append(buffer, 0, read);
                    }
                }

                // truncate if log too big
                if (logs.length() > MAX_LOG_SIZE)
                    logs.delete(0,logs.length() - MAX_LOG_SIZE);

                // Try to get wownerod version and build number
                if (version == null) {
                    int i = logs.toString().indexOf("src/daemon/main.cpp");
                    if (i != -1) {
                        int j = logs.toString().substring(i).indexOf("Wownero '");
                        if (j != -1) {
                            int k = logs.toString().substring(i+j).indexOf(")");
                            version =  logs.toString().substring(i+j,i+j+k+1);
                        }
                    }
                }

                // Update Height and target
                int i = logs.toString().lastIndexOf("Height:");
                if (i > 0) {
                    height = "";
                    target = "";
                    while (logs.charAt(i) != ' ') i++;
                    while (logs.charAt(i) != ',') {
                        height += logs.charAt(i);
                        i++;
                    }
                    i += 2;
                    while (logs.charAt(i) != ' ') i++;
                    i++;
                    while (logs.charAt(i) != ' ') {
                        target += logs.charAt(i);
                        i++;
                    }
                }

                // Download speed
                i = logs.toString().lastIndexOf("Downloading at ");
                if (i > 0) {
                    downloading = "";
                    i += 15;
                    while (logs.charAt(i) != ' ') {
                        downloading += logs.charAt(i);
                        i++;
                    }
                    i++;
                }

                // Number of peers
                i = logs.toString().lastIndexOf(" peers\n");
                if (i > 0) {
                    i --;
                    peers = "";
                    while (logs.charAt(i) >= '0' && logs.charAt(i) <= '9') i--;
                    i++;
                    while (logs.charAt(i) != ' ') {
                        peers += logs.charAt(i);
                        i++;
                    }
                }
            }

        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void getSyncInfo()  {
        try {
            // if process not already terminated
            if (writer != null) {
                writer.write("sync_info\n");
                writer.flush();
            }
        } catch (IOException e) {
            Log.e(TAG,"getSyncInfo: " +e.getMessage());
        }
    }

    public void exit() {
        // sending an exit twice might make it waiting forever
        if (processState == ProcessState.STOPPING)
            return;

        try {
            if (writer != null) {
                writer.write("exit\n");
                writer.flush();
            }
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
        processState = ProcessState.STOPPING;
    }

    public String getEnv(Settings pref) {
        ArrayList<String> a = new ArrayList<String>();

        a.add("--data-dir "+pref.getDataDir());
        if (pref.getLogFile() != null) a.add("--log-file "+pref.getLogFile());
        if (pref.getLogLevel() > 0) a.add("--log-level "+pref.getLogLevel());
        if (pref.getIsTestnet()) a.add("--testnet");
        if (pref.getIsStageNet()) a.add("--stagenet");
        if (pref.getBlockSyncSize() != 0) a.add("--block-sync-size "+pref.getBlockSyncSize());
        if (pref.getZmqRpcPort() != 0) a.add("--zmq-rpc-bind-port "+pref.getZmqRpcPort());
        if (pref.getP2pBindPort() != 0) a.add("--p2p-bind-port "+pref.getP2pBindPort());
        if (pref.getRpcBindPort() != 0) a.add("--rpc-bind-port "+pref.getRpcBindPort());
        if (pref.getAddExclusiveNode() != null) a.add("--add-exclusive-node "+pref.getAddExclusiveNode());
        if (pref.getSeedNode() != null) a.add("--seed-node "+pref.getSeedNode());
        if (pref.getOutPeers() != -1) a.add("--out-peers "+pref.getOutPeers());
        if (pref.getInPeers() != -1) a.add("--in-peers "+pref.getInPeers());
        if (pref.getLimitRateUp() != -1) a.add("--limit-rate-up "+pref.getLimitRateUp());
        if (pref.getLimitRateDown() != -1) a.add("--limit-rate-down "+pref.getLimitRateDown());
        if (pref.getLimitRate() != -1) a.add("--limit-rate "+pref.getLimitRate());
        if (pref.getBoostrapDaemonAdress() != null) a.add("--bootstrap-daemon-address "+pref.getBoostrapDaemonAdress());
        if (pref.getBoostrapDaemonLogin() != null) a.add("--bootstrap-daemon-login "+pref.getBoostrapDaemonLogin());
        if (pref.getPeerNode() != null) a.add("--add-peer "+pref.getPeerNode());
        if (pref.getAddPriorityNode() != null) a.add("--add-priority-node "+pref.getAddPriorityNode());
        if (pref.getRestrictedRpc()) a.add("--restricted-rpc ");
        if (pref.isFastBlocSync()) a.add("--fast-block-sync 1 ");

        String ret = "";
        for (String s : a) {
            ret += s + " ";
        }
        // set Safe and LMDB sync mode. This should prevent LMDB corruption when android kill the daemon.
        ret += "--db-sync-mode safe:sync ";
        return ret;
    }

    public String getPeers() {
        return peers;
    }

    public String getHeight() {
        return height;
    }

    public String getTarget() {
        return target;
    }


    public String getVersion() {
        return version;
    }

    public String getLogs() {
        return logs == null ? "" : logs.toString();
    }

    public boolean isStopped() {
        return processState == ProcessState.STOPPED;
    }
    public boolean isStarting() { return processState == ProcessState.STARTING; }

    public String getDownloading() {
        return downloading;
    }

    public boolean isAlive() {
        try {
            ProcessBuilder builder = new ProcessBuilder("ps"); // | /system/bin/grep aeond");
            builder.redirectErrorStream(true);
            Map<String, String> env = builder.environment();
            env.put("PATH","/bin:/system/bin");
            Process process = builder.start();

            InputStreamReader isReader = new InputStreamReader(process.getInputStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine = null;
            while ((strLine = bReader.readLine()) != null) {
                int i = strLine.lastIndexOf("wownerod");
                if (i > 0 && !(strLine.length() > i+8 && strLine.charAt(i+8) == 'a')) {
                    processState = ProcessState.RUNNING;
                    return true;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG,"Got exception using getting the PID"+ ex.getMessage());
        }
        processState = ProcessState.STOPPED;
        return false;
    }

    /**
     * If the SD card is in use, try to create the folder.
     * @param pref
     * @return
     */
    private boolean checkSDCard(Settings pref) {
        if (pref.isUseSDCard() && pref.getSdCardPath() != null) {
            // Try to create location
            File file = new File(pref.getSdCardPath());
            boolean r = file.exists();
            if (!r) r = file.mkdir();
            //Log.d(TAG, "Check SD card " + file.exists() + "  Writable " + " r=" + r + " " + file.canWrite() + " " + file.getAbsolutePath());
            return r;
        } else if (pref.isUseCustomStorage() && pref.getCustomStoragePath() != null) {
            // Try to create location
            File file = new File(pref.getCustomStoragePath());
            boolean r = file.exists();
            if (!r) r = file.mkdir();
            //Log.d(TAG, "Check SD card " + file.exists() + "  Writable " + " r=" + r + " " + file.canWrite() + " " + file.getAbsolutePath());
            return r;
        } else
            return true;
    }
}
