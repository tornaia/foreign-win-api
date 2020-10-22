package com.github.tornaia.foreign.win.api.cfapi.api;

import java.nio.file.Path;

public class CreatePlaceholderCommand {

    private final Path syncRootPath;
    private final String filename;
    private final long fileSize;
    private final long creationTime;
    private final long modifiedTime;
    private final long lastAccessedTime;

    public CreatePlaceholderCommand(Path syncRootPath, String filename, long fileSize, long creationTime, long modifiedTime, long lastAccessedTime) {
        this.syncRootPath = syncRootPath;
        this.filename = filename;
        this.fileSize = fileSize;
        this.creationTime = creationTime;
        this.modifiedTime = modifiedTime;
        this.lastAccessedTime = lastAccessedTime;
    }

    public Path getSyncRootPath() {
        return syncRootPath;
    }

    public String getFilename() {
        return filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public String toString() {
        return "CreatePlaceholderCommand{" +
                "syncRootPath=" + syncRootPath +
                ", filename='" + filename + '\'' +
                ", fileSize=" + fileSize +
                ", creationTime=" + creationTime +
                ", modifiedTime=" + modifiedTime +
                ", lastAccessedTime=" + lastAccessedTime +
                '}';
    }
}
