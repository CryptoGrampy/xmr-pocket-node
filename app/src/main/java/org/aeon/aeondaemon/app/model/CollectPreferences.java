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

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Map;

public class CollectPreferences {
    public static Settings collectedPreferences = new Settings();
    private static final String TAG = CollectPreferences.class.getSimpleName();

    public static void collect(SharedPreferences prefs) {
        Map<String,?> keys = prefs.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().equals("Testnet")) {
                Boolean t = prefs.getBoolean("Testnet",false);
                collectedPreferences.setIsTestnet(t.booleanValue());
            }
            if (entry.getKey().equals("Stagenet")) {
                Boolean t = prefs.getBoolean("Stagenet",false);
                collectedPreferences.setIsStageNet(t.booleanValue());
            }
            if (entry.getKey().equals("p2pBindPort")) {
                String t = prefs.getString("p2pBindPort","");
                if (! t.equals("")) collectedPreferences.setP2pBindPort(Integer.parseInt(t));
                else collectedPreferences.setP2pBindPort(0);
            }
            if (entry.getKey().equals("rpcBindPort")) {
                String t = prefs.getString("rpcBindPort","");
                if (! t.equals("")) collectedPreferences.setRpcBindPort(Integer.parseInt(t));
                else collectedPreferences.setRpcBindPort(0);
            }
            if (entry.getKey().equals("zmqBindPort")) {
                String t = prefs.getString("zmqBindPort","");
                if (! t.equals("")) collectedPreferences.setZmqRpcPort(Integer.parseInt(t));
                else collectedPreferences.setZmqRpcPort(0);
            }
            if (entry.getKey().equals("blockSyncSize")) {
                String t = prefs.getString("blockSyncSize","");
                if (! t.equals("")) collectedPreferences.setBlockSyncSize(Integer.parseInt(t));
                else collectedPreferences.setBlockSyncSize(0);
            }
            if (entry.getKey().equals("limitRate")) {
                String t = prefs.getString("limitRate","");
                if (! t.equals("")) collectedPreferences.setLimitRate(Integer.parseInt(t));
                else collectedPreferences.setLimitRate(-1);
            }
            if (entry.getKey().equals("limitRateDown")) {
                String t = prefs.getString("limitRateDown","");
                if (! t.equals("")) collectedPreferences.setLimitRateDown(Integer.parseInt(t));
                else collectedPreferences.setLimitRateDown(-1);
            }
            if (entry.getKey().equals("limitRateUp")) {
                String t = prefs.getString("limitRateUp","");
                if (! t.equals("")) collectedPreferences.setLimitRateUp(Integer.parseInt(t));
                else collectedPreferences.setLimitRateUp(-1);
            }
            if (entry.getKey().equals("outPeers")) {
                String t = prefs.getString("outPeers","");
                if (! t.equals("")) collectedPreferences.setOutPeers(Integer.parseInt(t));
                else collectedPreferences.setOutPeers(-1);
            }
            if (entry.getKey().equals("inPeers")) {
                String t = prefs.getString("inPeers","");
                if (! t.equals("")) collectedPreferences.setInPeers(Integer.parseInt(t));
                else collectedPreferences.setInPeers(-1);
            }
            if (entry.getKey().equals("peerNode")) {
                String t = prefs.getString("peerNode","");
                if (! t.equals("")) collectedPreferences.setPeerNode(t);
                else collectedPreferences.setPeerNode(null);
            }
            if (entry.getKey().equals("exclusiveNode")) {
                String t = prefs.getString("exclusiveNode","");
                if (! t.equals("")) collectedPreferences.setAddExclusiveNode(t);
                else collectedPreferences.setAddExclusiveNode(null);
            }
            if (entry.getKey().equals("priorityNode")) {
                String t = prefs.getString("priorityNode","");
                if (! t.equals("")) collectedPreferences.setAddPriorityNode(t);
                else collectedPreferences.setAddPriorityNode(null);
            }
            if (entry.getKey().equals("restricted_rpc")) {
                Boolean t = prefs.getBoolean("restricted_rpc",true);
                collectedPreferences.setRestrictedRpc(t.booleanValue());
            }
            if (entry.getKey().equals("use_sd_card")) {
                Boolean t = prefs.getBoolean("use_sd_card",false);
                collectedPreferences.setUseSDCard(t.booleanValue());
            }
            if (entry.getKey().equals("sd_storage")) {
                String t = prefs.getString("sd_storage","");
                if (! t.equals("")) collectedPreferences.setSdCardPath(t);
                else collectedPreferences.setSdCardPath(null);
            }
            if (entry.getKey().equals("use_custom_storage")) {
                Boolean t = prefs.getBoolean("use_custom_storage",false);
                collectedPreferences.setUseCustomStorage(t.booleanValue());
            }
            if (entry.getKey().equals("sd_custom_storage")) {
                String t = prefs.getString("sd_custom_storage","");
                if (! t.equals("")) collectedPreferences.setCustomStoragePath(t);
                else collectedPreferences.setCustomStoragePath(null);
            }
            if (entry.getKey().equals("loglevel")) {
                Integer t = Integer.parseInt(prefs.getString("loglevel","0"));
                collectedPreferences.setLogLevel(t.intValue());
            }
            if (entry.getKey().equals("fast_bloc_sync")) {
                Boolean t = prefs.getBoolean("fast_bloc_sync",false);
                collectedPreferences.setFastBlocSync(t.booleanValue());
            }

        }
    }

    public static String getExternalStoragePath() {
        String externalStoragePath = System.getenv("SECONDARY_STORAGE");
        if (externalStoragePath == null) return null;

        boolean hasExternalStorage = false;
        File file = new File(externalStoragePath);
        if (file.exists()) hasExternalStorage = file.getUsableSpace() > 0;
        //Log.d(TAG,"External storage: "+hasExternalStorage);

        if (!hasExternalStorage) return null;
        else return externalStoragePath+ "/wownero";
    }

    public static String getCustomPath() {
        File file = new File(Environment.getExternalStorageDirectory(),"wownero");
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file.getAbsolutePath();
    }
}
