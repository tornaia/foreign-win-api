package com.github.tornaia.foreign.win.api.cfapi.api;

import java.nio.file.Path;

public class UnregisterSyncRootCommand {

    private final Path syncRootPath;

    public UnregisterSyncRootCommand(Path syncRootPath) {
        this.syncRootPath = syncRootPath;
    }

    public Path getSyncRootPath() {
        return syncRootPath;
    }

    @Override
    public String toString() {
        return "UnregisterSyncRootCommand{" +
                "syncRootPath=" + syncRootPath +
                '}';
    }
}
