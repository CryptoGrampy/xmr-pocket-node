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

public class Settings {
    private String dataDir=null;
    private String logFile="/dev/null";
    private int logLevel=0;
    private Boolean isTestnet = false;
    private Boolean isStageNet = false;
    private int blockSyncSize = 0;
    private int zmqRpcPort=0;
    private int p2pBindPort=0;
    private int rpcBindPort=0;
    private String addExclusiveNode=null;
    private String addPriorityNode=null;
    private String adress="Wmsmmjtzk269mpmWm9CTC8DXDs9FZmKdrbFqm1gAmdFxJwEtsZU9PxDJDLYxtLsoSSjn6y6iXYcXVfgYSAGC5vrL13rDqUs4n";
    private String seedNode=null;
    private String peerNode=null;
    private int outPeers=-1;
    private int inPeers=-1;
    private int limitRateUp=-1;
    private int limitRateDown=-1;
    private int limitRate=-1;
    private String boostrapDaemonAdress=null;
    private String boostrapDaemonLogin=null;
    private Boolean restrictedRpc=true;
    private String sdCardPath=null;
    private boolean useSDCard=false;
    private String customStoragePath=null;
    private boolean useCustomStorage=false;
    private boolean fastBlocSync=false;

    public boolean isUseSDCard() {
        return useSDCard;
    }

    public void setUseSDCard(boolean useSDCard) {
        this.useSDCard = useSDCard;
    }

    public void setSdCardPath(String sdCardPath) {
        this.sdCardPath = sdCardPath;
    }

    public void setTestnet(Boolean testnet) {
        isTestnet = testnet;
    }

    public void setStageNet(Boolean stageNet) {
        isStageNet = stageNet;
    }


    public String getSdCardPath() {
        return sdCardPath;
    }

    public String getDataDir() {
        return dataDir;
    }
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
    public String getLogFile() {
        return logFile;
    }
    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }
    public Boolean getIsTestnet() {
        return isTestnet;
    }
    public void setIsTestnet(Boolean isTestnet) {
        this.isTestnet = isTestnet;
    }
    public Boolean getIsStageNet() {
        return isStageNet;
    }
    public void setIsStageNet(Boolean isStageNet) {
        this.isStageNet = isStageNet;
    }
    public int getBlockSyncSize() {
        return blockSyncSize;
    }
    public void setBlockSyncSize(int blockSyncSize) {
        this.blockSyncSize = blockSyncSize;
    }
    public int getZmqRpcPort() {
        return zmqRpcPort;
    }
    public void setZmqRpcPort(int zmqRpcPort) {
        this.zmqRpcPort = zmqRpcPort;
    }
    public int getP2pBindPort() {
        return p2pBindPort;
    }
    public void setP2pBindPort(int p2pBindPort) {
        this.p2pBindPort = p2pBindPort;
    }
    public int getRpcBindPort() {
        return rpcBindPort;
    }
    public void setRpcBindPort(int rpcBindPort) {
        this.rpcBindPort = rpcBindPort;
    }
    public String getAddExclusiveNode() {
        return addExclusiveNode;
    }
    public void setAddExclusiveNode(String addExclusiveNode) {
        this.addExclusiveNode = addExclusiveNode;
    }
    public String getAdress() {
        return adress;
    }
    public void setAdress(String adress) {
        this.adress = adress;
    }
    public String getSeedNode() {
        return seedNode;
    }
    public void setSeedNode(String seedNode) {
        this.seedNode = seedNode;
    }
    public int getOutPeers() {
        return outPeers;
    }
    public void setOutPeers(int outPeers) {
        this.outPeers = outPeers;
    }
    public int getInPeers() {
        return inPeers;
    }
    public void setInPeers(int inPeers) {
        this.inPeers = inPeers;
    }
    public int getLimitRateUp() {
        return limitRateUp;
    }
    public void setLimitRateUp(int limitRateUp) {
        this.limitRateUp = limitRateUp;
    }
    public int getLimitRateDown() {
        return limitRateDown;
    }
    public void setLimitRateDown(int limitRateDown) {
        this.limitRateDown = limitRateDown;
    }
    public int getLimitRate() {
        return limitRate;
    }
    public void setLimitRate(int limitRate) {
        this.limitRate = limitRate;
    }
    public String getBoostrapDaemonAdress() {
        return boostrapDaemonAdress;
    }
    public void setBoostrapDaemonAdress(String boostrapDaemonAdress) {
        this.boostrapDaemonAdress = boostrapDaemonAdress;
    }
    public String getBoostrapDaemonLogin() {
        return boostrapDaemonLogin;
    }
    public void setBoostrapDaemonLogin(String boostrapDaemonLogin) {
        this.boostrapDaemonLogin = boostrapDaemonLogin;
    }
    public String getPeerNode() {
        return peerNode;
    }
    public void setPeerNode(String peerNode) {
        this.peerNode = peerNode;
    }
    public String getAddPriorityNode() {
        return addPriorityNode;
    }
    public void setAddPriorityNode(String addPriorityNode) {
        this.addPriorityNode = addPriorityNode;
    }
    public Boolean getRestrictedRpc() {
        return restrictedRpc;
    }
    public void setRestrictedRpc(Boolean restrictedRpc) {
        this.restrictedRpc = restrictedRpc;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public Boolean getTestnet() {
        return isTestnet;
    }

    public Boolean getStageNet() {
        return isStageNet;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isFastBlocSync() {
        return fastBlocSync;
    }

    public void setFastBlocSync(boolean fastBlocSync) {
        this.fastBlocSync = fastBlocSync;
    }

    public String getCustomStoragePath() {
        return customStoragePath;
    }

    public void setCustomStoragePath(String customStoragePath) {
        this.customStoragePath = customStoragePath;
    }

    public boolean isUseCustomStorage() {
        return useCustomStorage;
    }

    public void setUseCustomStorage(boolean useCustomStorage) {
        this.useCustomStorage = useCustomStorage;
    }
}
