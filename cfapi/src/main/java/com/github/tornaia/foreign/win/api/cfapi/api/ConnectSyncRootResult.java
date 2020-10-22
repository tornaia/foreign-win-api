package com.github.tornaia.foreign.win.api.cfapi.api;

import com.github.tornaia.foreign.win.api.winbase.HResult;

public class ConnectSyncRootResult {

    private final HResult hResult;

    private ConnectSyncRootResult(HResult hResult) {
        this.hResult = hResult;
    }

    public static ConnectSyncRootResult ok() {
        return new ConnectSyncRootResult(HResult.OK);
    }

    public static ConnectSyncRootResult error(HResult hResult) {
        return new ConnectSyncRootResult(hResult);
    }

    public HResult getHResult() {
        return hResult;
    }

    @Override
    public String toString() {
        return "ConnectSyncRootResult{" +
                "hResult=" + hResult +
                '}';
    }
}
