package com.github.tornaia.foreign.win.api.cfapi.api;

import java.nio.file.Path;

public class RegisterSyncRootCommand {

    private final Path syncRootPath;
    private final String providerName;
    private final String providerVersion;

    public RegisterSyncRootCommand(Path syncRootPath, String providerName, String providerVersion) {
        this.syncRootPath = syncRootPath;
        this.providerName = providerName;
        this.providerVersion = providerVersion;
    }

    public Path getSyncRootPath() {
        return syncRootPath;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderVersion() {
        return providerVersion;
    }

    @Override
    public String toString() {
        return "RegisterSyncRootCommand{" +
                "syncRootPath=" + syncRootPath +
                ", providerName='" + providerName + '\'' +
                ", providerVersion='" + providerVersion + '\'' +
                '}';
    }
}
