package com.github.tornaia.foreign.win.api.cfapi.api;

import com.github.tornaia.foreign.win.api.winbase.HResult;

public class UnregisterSyncRootResult {

    private final HResult hResult;

    private UnregisterSyncRootResult(HResult hResult) {
        this.hResult = hResult;
    }

    public static UnregisterSyncRootResult ok() {
        return new UnregisterSyncRootResult(HResult.OK);
    }

    public static UnregisterSyncRootResult error(HResult hResult) {
        return new UnregisterSyncRootResult(hResult);
    }

    public HResult getHResult() {
        return hResult;
    }

    @Override
    public String toString() {
        return "UnregisterSyncRootResult{" +
                "hResult=" + hResult +
                '}';
    }
}
