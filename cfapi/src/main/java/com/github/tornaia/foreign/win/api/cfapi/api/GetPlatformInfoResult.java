package com.github.tornaia.foreign.win.api.cfapi.api;

import com.github.tornaia.foreign.win.api.winbase.HResult;

/**
 * https://docs.microsoft.com/en-us/windows/win32/api/cfapi/ns-cfapi-cf_platform_info
 */
public class GetPlatformInfoResult {

    private final HResult hResult;
    private final int buildNumber;
    private final int revisionNumber;
    private final int integrationNumber;

    private GetPlatformInfoResult(HResult hResult, int buildNumber, int revisionNumber, int integrationNumber) {
        this.hResult = hResult;
        this.buildNumber = buildNumber;
        this.revisionNumber = revisionNumber;
        this.integrationNumber = integrationNumber;
    }

    public static GetPlatformInfoResult ok(int buildNumber, int revisionNumber, int integrationNumber) {
        return new GetPlatformInfoResult(HResult.OK, buildNumber, revisionNumber, integrationNumber);
    }

    public static GetPlatformInfoResult error(HResult hResult) {
        return new GetPlatformInfoResult(hResult, -1, -1, -1);
    }

    public HResult getHResult() {
        return hResult;
    }

    /**
     * The build number of the Windows platform version. Changes when the platform is serviced by a Windows update.
     */
    public int getBuildNumber() {
        return buildNumber;
    }

    /**
     * The revision number of the Windows platform version. Changes when the platform is serviced by a Windows update.
     */
    public int getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * The integration number of the Windows platform version. This is indicative of the platform capabilities, both in terms of API contracts and availability of bug fixes.
     */
    public int getIntegrationNumber() {
        return integrationNumber;
    }

    @Override
    public String toString() {
        return "GetPlatformInfoResult{" +
                "hResult=" + hResult +
                ", buildNumber=" + buildNumber +
                ", revisionNumber=" + revisionNumber +
                ", integrationNumber=" + integrationNumber +
                '}';
    }
}
