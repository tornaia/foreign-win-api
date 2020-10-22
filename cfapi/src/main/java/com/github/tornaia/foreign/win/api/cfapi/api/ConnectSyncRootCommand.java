package com.github.tornaia.foreign.win.api.cfapi.api;

import java.nio.file.Path;

public class ConnectSyncRootCommand {

    private final Path syncRootPath;
    private final FetchDataCallback fetchDataCallback;

    public ConnectSyncRootCommand(Path syncRootPath, FetchDataCallback fetchDataCallback) {
        this.syncRootPath = syncRootPath;
        this.fetchDataCallback = fetchDataCallback;
    }

    public Path getSyncRootPath() {
        return syncRootPath;
    }

    public FetchDataCallback getFetchDataCallback() {
        return fetchDataCallback;
    }

    @Override
    public String toString() {
        return "ConnectSyncRootCommand{" +
                "syncRootPath=" + syncRootPath +
                ", fetchDataCallback=" + fetchDataCallback +
                '}';
    }

    public static class CallbackInfo {

        private final int structSize;
        // TODO add remaining fields
        // private final CF_CONNECTION_KEY connectionKey;
        // LPVOID              CallbackContext;
        private final String volumeGuidName;
        private final String volumeDosName;
        private final int volumeSerialNumber;
        private final long syncRootFileId;
        // LPCVOID             SyncRootIdentity;
        // DWORD               SyncRootIdentityLength;
        private final long fileId;
        private final long fileSize;
        // LPCVOID             FileIdentity;
        // DWORD               FileIdentityLength;
        private final String normalizedPath;
        // CF_TRANSFER_KEY     TransferKey;
        private final byte priorityHint;
        // PCORRELATION_VECTOR CorrelationVector;
        private final ProcessInfo processInfo;
        // CF_REQUEST_KEY      RequestKey;

        public CallbackInfo(int structSize, String volumeGuidName, String volumeDosName, int volumeSerialNumber, long syncRootFileId, long fileId, long fileSize, String normalizedPath, byte priorityHint, ProcessInfo processInfo) {
            this.structSize = structSize;
            this.volumeGuidName = volumeGuidName;
            this.volumeDosName = volumeDosName;
            this.volumeSerialNumber = volumeSerialNumber;
            this.syncRootFileId = syncRootFileId;
            this.fileId = fileId;
            this.fileSize = fileSize;
            this.normalizedPath = normalizedPath;
            this.priorityHint = priorityHint;
            this.processInfo = processInfo;
        }

        public int getStructSize() {
            return structSize;
        }

        public String getVolumeGuidName() {
            return volumeGuidName;
        }

        public String getVolumeDosName() {
            return volumeDosName;
        }

        public int getVolumeSerialNumber() {
            return volumeSerialNumber;
        }

        public long getSyncRootFileId() {
            return syncRootFileId;
        }

        public long getFileId() {
            return fileId;
        }

        public long getFileSize() {
            return fileSize;
        }

        public String getNormalizedPath() {
            return normalizedPath;
        }

        public int getPriorityHint() {
            return priorityHint;
        }

        public ProcessInfo getProcessInfo() {
            return processInfo;
        }

        @Override
        public String toString() {
            return "CallbackInfo{" +
                    "structSize=" + structSize +
                    ", volumeGuidName='" + volumeGuidName + '\'' +
                    ", volumeDosName='" + volumeDosName + '\'' +
                    ", volumeSerialNumber=" + volumeSerialNumber +
                    ", syncRootFileId=" + syncRootFileId +
                    ", fileId=" + fileId +
                    ", fileSize=" + fileSize +
                    ", normalizedPath='" + normalizedPath + '\'' +
                    ", priorityHint=" + priorityHint +
                    ", processInfo=" + processInfo +
                    '}';
        }

        public static class ProcessInfo {

            private final int structSize;
            private final int processId;
            private final String imagePath;
            private final String packageName;
            private final String applicationId;
            private final String commandLine;
            private final int sessionId;

            public ProcessInfo(int structSize, int processId, String imagePath, String packageName, String applicationId, String commandLine, int sessionId) {
                this.structSize = structSize;
                this.processId = processId;
                this.imagePath = imagePath;
                this.packageName = packageName;
                this.applicationId = applicationId;
                this.commandLine = commandLine;
                this.sessionId = sessionId;
            }

            public int getStructSize() {
                return structSize;
            }

            public int getProcessId() {
                return processId;
            }

            public String getImagePath() {
                return imagePath;
            }

            public String getPackageName() {
                return packageName;
            }

            public String getApplicationId() {
                return applicationId;
            }

            public String getCommandLine() {
                return commandLine;
            }

            public int getSessionId() {
                return sessionId;
            }

            @Override
            public String toString() {
                return "ProcessInfo{" +
                        "structSize=" + structSize +
                        ", processId=" + processId +
                        ", imagePath='" + imagePath + '\'' +
                        ", packageName='" + packageName + '\'' +
                        ", applicationId='" + applicationId + '\'' +
                        ", commandLine='" + commandLine + '\'' +
                        ", sessionId=" + sessionId +
                        '}';
            }
        }
    }

    public static class FetchDataCallbackParameters {

        private final int paramSize;
        // TODO convert to enum CF_CALLBACK_FETCH_DATA_FLAGS
        private final int flags;
        private final long requiredFileOffset;
        private final long requiredLength;
        private final long optionalFileOffset;
        private final long optionalLength;
        private final long lastDehydrationTime;
        // TODO convert to enum CF_CALLBACK_DEHYDRATION_REASON
        private final int lastDehydrationReason;

        public FetchDataCallbackParameters(int paramSize, int flags, long requiredFileOffset, long requiredLength, long optionalFileOffset, long optionalLength, long lastDehydrationTime, int lastDehydrationReason) {
            this.paramSize = paramSize;
            this.flags = flags;
            this.requiredFileOffset = requiredFileOffset;
            this.requiredLength = requiredLength;
            this.optionalFileOffset = optionalFileOffset;
            this.optionalLength = optionalLength;
            this.lastDehydrationTime = lastDehydrationTime;
            this.lastDehydrationReason = lastDehydrationReason;
        }

        public int getParamSize() {
            return paramSize;
        }

        public int getFlags() {
            return flags;
        }

        public long getRequiredFileOffset() {
            return requiredFileOffset;
        }

        public long getRequiredLength() {
            return requiredLength;
        }

        public long getOptionalFileOffset() {
            return optionalFileOffset;
        }

        public long getOptionalLength() {
            return optionalLength;
        }

        public long getLastDehydrationTime() {
            return lastDehydrationTime;
        }

        public int getLastDehydrationReason() {
            return lastDehydrationReason;
        }

        @Override
        public String toString() {
            return "FetchDataCallbackParameters{" +
                    "paramSize=" + paramSize +
                    ", flags=" + flags +
                    ", requiredFileOffset=" + requiredFileOffset +
                    ", requiredLength=" + requiredLength +
                    ", optionalFileOffset=" + optionalFileOffset +
                    ", optionalLength=" + optionalLength +
                    ", lastDehydrationTime=" + lastDehydrationTime +
                    ", lastDehydrationReason=" + lastDehydrationReason +
                    '}';
        }
    }

    @FunctionalInterface
    public interface FetchDataCallback {
        void apply(CallbackInfo callbackInfo, FetchDataCallbackParameters fetchDataCallbackParameters);
    }
}
