package com.github.tornaia.foreign.win.api.cfapi;

import com.github.tornaia.foreign.win.api.cfapi.api.ConnectSyncRootCommand;
import com.github.tornaia.foreign.win.api.cfapi.api.ConnectSyncRootResult;
import com.github.tornaia.foreign.win.api.cfapi.api.CreatePlaceholderCommand;
import com.github.tornaia.foreign.win.api.cfapi.api.CreatePlaceholderResult;
import com.github.tornaia.foreign.win.api.cfapi.api.GetPlatformInfoResult;
import com.github.tornaia.foreign.win.api.cfapi.api.RegisterSyncRootCommand;
import com.github.tornaia.foreign.win.api.cfapi.api.RegisterSyncRootResult;
import com.github.tornaia.foreign.win.api.cfapi.api.UnregisterSyncRootCommand;
import com.github.tornaia.foreign.win.api.cfapi.api.UnregisterSyncRootResult;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.ALLOCATE_CF_CALLBACK$CF_CALLBACK;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CALLBACK_INFO;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CALLBACK_PARAMETERS;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CALLBACK_REGISTRATION;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CONNECTION_KEY;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_FS_METADATA;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_HYDRATION_POLICY;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_PLACEHOLDER_CREATE_INFO;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_PLATFORM_INFO;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_POPULATION_POLICY;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_SYNC_POLICIES;
import com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_SYNC_REGISTRATION;
import com.github.tornaia.foreign.win.api.winbase.HResult;
import com.github.tornaia.foreign.win.api.winbase.MemoryUtils;
import com.github.tornaia.foreign.win.api.winbase.internal.winbase_h.FILE_BASIC_INFO;
import com.github.tornaia.foreign.win.api.winnt.internal.winnt_h.LARGE_INTEGER;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CALLBACK_TYPE_CANCEL_FETCH_DATA;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CALLBACK_TYPE_FETCH_DATA;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CALLBACK_TYPE_NONE;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CONNECT_FLAG_REQUIRE_FULL_FILE_PATH;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CONNECT_FLAG_REQUIRE_PROCESS_INFO;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_CREATE_FLAG_NONE;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_HARDLINK_POLICY_NONE;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_HYDRATION_POLICY_FULL;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_HYDRATION_POLICY_MODIFIER_NONE;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_INSYNC_POLICY_NONE;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_PLACEHOLDER_CREATE_FLAG_MARK_IN_SYNC;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_PLACEHOLDER_MANAGEMENT_POLICY_DEFAULT;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_POPULATION_POLICY_ALWAYS_FULL;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_POPULATION_POLICY_MODIFIER_NONE;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_PROCESS_INFO;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CF_REGISTER_FLAG_MARK_IN_SYNC_ON_ROOT;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CfConnectSyncRoot;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CfCreatePlaceholders;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CfGetPlatformInfo;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CfRegisterSyncRoot;
import static com.github.tornaia.foreign.win.api.cfapi.internal.cfapi_h.CfUnregisterSyncRoot;
import static com.github.tornaia.foreign.win.api.winnt.internal.winnt_h.FILE_ATTRIBUTE_NORMAL;

/**
 * Starting in Windows 10, version 1709, Windows provides the cloud files API.
 * This API consists of several native Win32 and WinRT APIs that formalize
 * support for cloud sync engines, and handles tasks such as creating and
 * managing placeholder files and directories. Users of this API are typically
 * sync providers and to some extent, Windows applications.
 * <p>
 * https://docs.microsoft.com/en-us/windows/win32/api/_cloudapi/
 */
public class CloudFilterAPI {

    static {
        try {
            System.loadLibrary("cldapi");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Returns information for the cloud files platform.
     * This is intended for sync providers running on multiple versions of Windows.
     *
     * @return the platform version information
     */
    public static GetPlatformInfoResult getPlatformInfo() {
        try (MemorySegment addressablePlatformVersion = CF_PLATFORM_INFO.allocatePointer()) {
            int result = CfGetPlatformInfo(addressablePlatformVersion);

            HResult hResult = HResult.parse(result);
            boolean ok = hResult == HResult.OK;
            if (!ok) {
                return GetPlatformInfoResult.error(hResult);
            }

            int buildNumber = CF_PLATFORM_INFO.BuildNumber$get(addressablePlatformVersion);
            int revisionNumber = CF_PLATFORM_INFO.RevisionNumber$get(addressablePlatformVersion);
            // TODO for unknown reason integration number call fails
            // int integrationNumber = cfapi_h.CF_PLATFORM_INFO.IntegrationNumber$get(addressablePlatformVersion);
            return GetPlatformInfoResult.ok(buildNumber, revisionNumber, -1);
        }
    }

    /**
     * Performs a one time sync root registration.
     */
    public static RegisterSyncRootResult registerSyncRoot(RegisterSyncRootCommand registerSyncRootCommand) {
        String syncRootPath = registerSyncRootCommand.getSyncRootPath().toAbsolutePath().toString();
        String providerName = registerSyncRootCommand.getProviderName();
        String providerVersion = registerSyncRootCommand.getProviderVersion();
        try (MemorySegment SyncRootPath = MemoryUtils.toLPCWSTR(syncRootPath);
             MemorySegment Registration = CF_SYNC_REGISTRATION.allocate();
             MemorySegment ProviderName = MemoryUtils.toLPCWSTR(providerName);
             MemorySegment ProviderVersion = MemoryUtils.toLPCWSTR(providerVersion);
             MemorySegment Policies = CF_SYNC_POLICIES.allocate()) {

            // Registration
            CF_SYNC_REGISTRATION.StructSize$set(Registration, (int) CF_SYNC_REGISTRATION.sizeof());
            CF_SYNC_REGISTRATION.ProviderName$set(Registration, ProviderName.address());
            CF_SYNC_REGISTRATION.ProviderVersion$set(Registration, ProviderVersion.address());
            CF_SYNC_REGISTRATION.SyncRootIdentity$set(Registration, MemoryAddress.NULL);
            CF_SYNC_REGISTRATION.SyncRootIdentityLength$set(Registration, 0);
            CF_SYNC_REGISTRATION.FileIdentity$set(Registration, MemoryAddress.NULL);
            CF_SYNC_REGISTRATION.FileIdentityLength$set(Registration, 0);

            // Policies
            CF_SYNC_POLICIES.StructSize$set(Policies, (int) CF_SYNC_POLICIES.sizeof());
            CF_HYDRATION_POLICY.Primary$set(CF_SYNC_POLICIES.Hydration$slice(Policies), (short) CF_HYDRATION_POLICY_FULL());
            CF_HYDRATION_POLICY.Modifier$set(CF_SYNC_POLICIES.Hydration$slice(Policies), (short) CF_HYDRATION_POLICY_MODIFIER_NONE());
            CF_POPULATION_POLICY.Primary$set(CF_SYNC_POLICIES.Population$slice(Policies), (short) CF_POPULATION_POLICY_ALWAYS_FULL());
            CF_POPULATION_POLICY.Modifier$set(CF_SYNC_POLICIES.Population$slice(Policies), (short) CF_POPULATION_POLICY_MODIFIER_NONE());

            CF_SYNC_POLICIES.InSync$set(Policies, CF_INSYNC_POLICY_NONE());
            CF_SYNC_POLICIES.HardLink$set(Policies, CF_HARDLINK_POLICY_NONE());
            CF_SYNC_POLICIES.PlaceholderManagement$set(Policies, CF_PLACEHOLDER_MANAGEMENT_POLICY_DEFAULT());

            // Flags
            int RegisterFlags = CF_REGISTER_FLAG_MARK_IN_SYNC_ON_ROOT();

            int result = CfRegisterSyncRoot(SyncRootPath, Registration, Policies, RegisterFlags);

            HResult hResult = HResult.parse(result);
            boolean ok = hResult == HResult.OK;
            if (!ok) {
                return RegisterSyncRootResult.error(hResult);
            }

            return RegisterSyncRootResult.ok();
        }
    }

    /**
     * Initiates bi-directional communication between a sync provider and the sync filter API.
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/cfapi/ns-cfapi-cf_callback_registration">CF_CALLBACK_REGISTRATION structure (cfapi.h)</a>
     */
    public static ConnectSyncRootResult connectSyncRoot(ConnectSyncRootCommand connectSyncRootCommand) {
        String syncRootPath = connectSyncRootCommand.getSyncRootPath().toAbsolutePath().toString();
        try (MemorySegment SyncRootPath = MemoryUtils.toLPCWSTR(syncRootPath);
             MemorySegment CallbackTable = CF_CALLBACK_REGISTRATION.allocateArray(3);
             MemorySegment ConnectionKey = CF_CONNECTION_KEY.allocate()) {

            MemorySegment onFetchDataCallback = ALLOCATE_CF_CALLBACK$CF_CALLBACK.allocate((CallbackInfo, CallbackParameters) -> {
                System.out.print("Fetch data ");
                MemorySegment CfCallbackInfo = CallbackInfo.asSegmentRestricted(CF_CALLBACK_INFO.sizeof());
                MemorySegment CfCallbackParameters = CallbackParameters.asSegmentRestricted(CF_CALLBACK_PARAMETERS.sizeof());

                // CallbackInfo.ProcessInfo
                MemorySegment CfProcessInfo = CF_CALLBACK_INFO.ProcessInfo$get(CfCallbackInfo).asSegmentRestricted(CF_PROCESS_INFO.sizeof());
                int CfProcessInfoStructSize = CF_PROCESS_INFO.StructSize$get(CfProcessInfo);
                int ProcessId = CF_PROCESS_INFO.ProcessId$get(CfProcessInfo);
                String ImagePath = MemoryUtils.parsePCWSTR(CF_PROCESS_INFO.ImagePath$get(CfProcessInfo));
                String PackageName = MemoryUtils.parsePCWSTR(CF_PROCESS_INFO.PackageName$get(CfProcessInfo));
                String ApplicationId = MemoryUtils.parsePCWSTR(CF_PROCESS_INFO.ApplicationId$get(CfProcessInfo));
                String CommandLine = MemoryUtils.parsePCWSTR(CF_PROCESS_INFO.CommandLine$get(CfProcessInfo));
                int SessionId = CF_PROCESS_INFO.SessionId$get(CfProcessInfo);
                ConnectSyncRootCommand.CallbackInfo.ProcessInfo ProcessInfo = new ConnectSyncRootCommand.CallbackInfo.ProcessInfo(CfProcessInfoStructSize, ProcessId, ImagePath, PackageName, ApplicationId, CommandLine, SessionId);

                // CallbackInfo
                int CfCallbackInfoStructSize = CF_CALLBACK_INFO.StructSize$get(CfCallbackInfo);
                String VolumeGuidName = MemoryUtils.parsePCWSTR(CF_CALLBACK_INFO.VolumeGuidName$get(CfCallbackInfo));
                String VolumeDosName = MemoryUtils.parsePCWSTR(CF_CALLBACK_INFO.VolumeDosName$get(CfCallbackInfo));
                int VolumeSerialNumber = CF_CALLBACK_INFO.VolumeSerialNumber$get(CfCallbackInfo);
                long SyncRootFileId = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_INFO.SyncRootFileId$slice(CfCallbackInfo));
                long FileId = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_INFO.FileId$slice(CfCallbackInfo));
                long FileSize = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_INFO.FileSize$slice(CfCallbackInfo));
                String NormalizedPath = MemoryUtils.parsePCWSTR(CF_CALLBACK_INFO.NormalizedPath$get(CfCallbackInfo));
                byte PriorityHint = CF_CALLBACK_INFO.PriorityHint$get(CfCallbackInfo);
                ConnectSyncRootCommand.CallbackInfo callbackInfo = new ConnectSyncRootCommand.CallbackInfo(CfCallbackInfoStructSize, VolumeGuidName, VolumeDosName, VolumeSerialNumber, SyncRootFileId, FileId, FileSize, NormalizedPath, PriorityHint, ProcessInfo);

                // FetchDataCallbackParameters
                int ParamSize = CF_CALLBACK_PARAMETERS.ParamSize$get(CfCallbackParameters);
                int Flags = CF_CALLBACK_PARAMETERS.FetchData.Flags$get(CfCallbackParameters);
                long RequiredFileOffset = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_PARAMETERS.FetchData.RequiredFileOffset$slice(CfCallbackParameters));
                long RequiredLength = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_PARAMETERS.FetchData.RequiredLength$slice(CfCallbackParameters));
                long OptionalFileOffset = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_PARAMETERS.FetchData.OptionalFileOffset$slice(CfCallbackParameters));
                long OptionalLength = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_PARAMETERS.FetchData.OptionalLength$slice(CfCallbackParameters));
                long LastDehydrationTime = LARGE_INTEGER.QuadPart$get(CF_CALLBACK_PARAMETERS.FetchData.LastDehydrationTime$slice(CfCallbackParameters));
                int LastDehydrationReason = CF_CALLBACK_PARAMETERS.FetchData.LastDehydrationReason$get(CfCallbackParameters);
                ConnectSyncRootCommand.FetchDataCallbackParameters fetchDataCallbackParameters = new ConnectSyncRootCommand.FetchDataCallbackParameters(ParamSize, Flags, RequiredFileOffset, RequiredLength, OptionalFileOffset, OptionalLength, LastDehydrationTime, LastDehydrationReason);

                ConnectSyncRootCommand.FetchDataCallback fetchDataCallback = connectSyncRootCommand.getFetchDataCallback();
                fetchDataCallback.apply(callbackInfo, fetchDataCallbackParameters);

                // TODO Lehet hogy a célterületre - ha ott placeholder van - létre kéne hozni egy "rendes" filet? Vagy javaból vagy a CreateFile lófasz alapján... hmm hmm
            });

            MemorySegment onCancelFetchDataCallback = ALLOCATE_CF_CALLBACK$CF_CALLBACK.allocate((CallbackInfo, CallbackParameters) -> {
                System.out.println("cancel fetch data");
                return;
            });

            // CallbackTable
            CF_CALLBACK_REGISTRATION.Type$set(CallbackTable, 0, CF_CALLBACK_TYPE_FETCH_DATA());
            CF_CALLBACK_REGISTRATION.Callback$set(CallbackTable, 0, onFetchDataCallback.address());

            CF_CALLBACK_REGISTRATION.Type$set(CallbackTable, 1, CF_CALLBACK_TYPE_CANCEL_FETCH_DATA());
            CF_CALLBACK_REGISTRATION.Callback$set(CallbackTable, 1, onCancelFetchDataCallback.address());

            // Note that the sync provider only needs to register implemented callbacks, and CF_CALLBACK_REGISTRATION should always end with CF_CALLBACK_REGISTRATION_END.
            // #define CF_CALLBACK_REGISTRATION_END {CF_CALLBACK_TYPE_NONE, NULL}
            CF_CALLBACK_REGISTRATION.Type$set(CallbackTable, 2, CF_CALLBACK_TYPE_NONE());
            CF_CALLBACK_REGISTRATION.Callback$set(CallbackTable, 2, MemoryAddress.NULL);

            // CallbackContext
            MemoryAddress CallbackContext = MemoryAddress.NULL;

            // Flags
            int ConnectFlags = CF_CONNECT_FLAG_REQUIRE_PROCESS_INFO() | CF_CONNECT_FLAG_REQUIRE_FULL_FILE_PATH();

            // ConnectionKey

            int result = CfConnectSyncRoot(SyncRootPath, CallbackTable, CallbackContext, ConnectFlags, ConnectionKey);

            HResult hResult = HResult.parse(result);
            boolean ok = hResult == HResult.OK;
            if (!ok) {
                return ConnectSyncRootResult.error(hResult);
            }

            return ConnectSyncRootResult.ok();
        }
    }

    public static CreatePlaceholderResult createPlaceholder(CreatePlaceholderCommand createPlaceholderCommand) {
        String syncRootPath = createPlaceholderCommand.getSyncRootPath().toAbsolutePath().toString();
        String filename = createPlaceholderCommand.getFilename();

        try (MemorySegment SyncRootPath = MemoryUtils.toLPCWSTR(syncRootPath);
             MemorySegment PlaceholderArray = CF_PLACEHOLDER_CREATE_INFO.allocateArray(1);
             MemorySegment RelativeFileName = MemoryUtils.toLPCWSTR(filename)) {

            // PlaceholderArray
            // FileIdentity, FileIdentityLength
            CF_PLACEHOLDER_CREATE_INFO.FileIdentity$set(PlaceholderArray, 0, RelativeFileName.address());
            CF_PLACEHOLDER_CREATE_INFO.FileIdentityLength$set(PlaceholderArray, 0, filename.length());
            CF_PLACEHOLDER_CREATE_INFO.RelativeFileName$set(PlaceholderArray, 0, RelativeFileName.address());
            // Flags
            CF_PLACEHOLDER_CREATE_INFO.Flags$set(PlaceholderArray, 0, CF_PLACEHOLDER_CREATE_FLAG_MARK_IN_SYNC());
            // FsMetadata
            MemorySegment FsMetadata = CF_PLACEHOLDER_CREATE_INFO.FsMetadata$slice(PlaceholderArray);
            // FsMetadata.FileSize
            LARGE_INTEGER.QuadPart$set(CF_FS_METADATA.FileSize$slice(FsMetadata), createPlaceholderCommand.getFileSize());
            // FsMetadata.BasicInfo
            MemorySegment BasicInfo = CF_FS_METADATA.BasicInfo$slice(FsMetadata);
            FILE_BASIC_INFO.FileAttributes$set(BasicInfo, FILE_ATTRIBUTE_NORMAL());
            LARGE_INTEGER.QuadPart$set(FILE_BASIC_INFO.CreationTime$slice(BasicInfo), MemoryUtils.toFileTime(createPlaceholderCommand.getCreationTime()));
            LARGE_INTEGER.QuadPart$set(FILE_BASIC_INFO.LastWriteTime$slice(BasicInfo), MemoryUtils.toFileTime(createPlaceholderCommand.getModifiedTime()));
            LARGE_INTEGER.QuadPart$set(FILE_BASIC_INFO.ChangeTime$slice(BasicInfo), MemoryUtils.toFileTime(createPlaceholderCommand.getModifiedTime()));
            LARGE_INTEGER.QuadPart$set(FILE_BASIC_INFO.LastAccessTime$slice(BasicInfo), MemoryUtils.toFileTime(createPlaceholderCommand.getLastAccessedTime()));

            int result = CfCreatePlaceholders(SyncRootPath, PlaceholderArray, 1, CF_CREATE_FLAG_NONE(), MemoryAddress.NULL);

            int outResult = CF_PLACEHOLDER_CREATE_INFO.Result$get(PlaceholderArray);
            if (result != outResult) {
                System.err.println("Must not happen, result and out param result mismatch, result: " + result + ", outResult: " + outResult);
            }

            HResult hResult = HResult.parse(result);
            boolean ok = hResult == HResult.OK;
            if (!ok) {
                return CreatePlaceholderResult.error(hResult);
            }

            return CreatePlaceholderResult.ok();
        }
    }

    /**
     * Unregisters a previously registered sync root.
     */
    public static UnregisterSyncRootResult unregisterSyncRoot(UnregisterSyncRootCommand unregisterSyncRootCommand) {
        String syncRootPath = unregisterSyncRootCommand.getSyncRootPath().toAbsolutePath().toString();
        try (MemorySegment SyncRootPath = MemoryUtils.toLPCWSTR(syncRootPath)) {

            int result = CfUnregisterSyncRoot(SyncRootPath);

            HResult hResult = HResult.parse(result);
            boolean ok = hResult == HResult.OK;
            if (!ok) {
                return UnregisterSyncRootResult.error(hResult);
            }

            return UnregisterSyncRootResult.ok();
        }
    }
}
