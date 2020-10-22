package com.github.tornaia.foreign.win.api.cfapi.api;

import com.github.tornaia.foreign.win.api.winbase.HResult;

public class RegisterSyncRootResult {

    private final HResult hResult;

    private RegisterSyncRootResult(HResult hResult) {
        this.hResult = hResult;
    }

    public static RegisterSyncRootResult ok() {
        return new RegisterSyncRootResult(HResult.OK);
    }

    public static RegisterSyncRootResult error(HResult hResult) {
        return new RegisterSyncRootResult(hResult);
    }

    public HResult getHResult() {
        return hResult;
    }

    @Override
    public String toString() {
        return "RegisterSyncRootResult{" +
                "hResult=" + hResult +
                '}';
    }
}
