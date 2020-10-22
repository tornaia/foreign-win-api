package com.github.tornaia.foreign.win.api.winbase;

import com.github.tornaia.foreign.win.api.winbase.internal.C;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MemoryUtils {

    private static final char TERMINATING_NULL_CHARACTER = 0;

    private MemoryUtils() {
    }

    public static String parsePCWSTR(@C("PCWSTR") MemoryAddress memoryAddress) {
        List<Character> chars = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            char c = MemoryAccess.getCharAtIndex_LE(memoryAddress.asSegmentRestricted(2 * (i + 1)), i);
            if (c == TERMINATING_NULL_CHARACTER) {
                break;
            }
            chars.add(c);
        }
        return chars
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static MemorySegment toLPCWSTR(String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_16LE);
        MemorySegment memorySegment = MemorySegment.allocateNative(bytes.length + 1);
        memorySegment.asByteBuffer().put(bytes).put((byte) TERMINATING_NULL_CHARACTER);
        return memorySegment;
    }

    /**
     * Converts epochMillis to FILETIME format, which is a 64-bit value representing the number of 100-nanosecond intervals since January 1, 1601 (UTC).
     *
     * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/minwinbase/ns-minwinbase-filetime">FILETIME structure (minwinbase.h)</a>
     * @see <a href="https://en.wikipedia.org/wiki/System_time">System time</a>
     */
    public static long toFileTime(long epochMillis) {
        long NANOS_BETWEEN_1601_AND_1970 = 116444736000000000L;
        return NANOS_BETWEEN_1601_AND_1970 + epochMillis * 10000L;
    }

    public static long toEpochMillis(long fileTime) {
        long NANOS_BETWEEN_1601_AND_1970 = 116444736000000000L;
        return (fileTime - NANOS_BETWEEN_1601_AND_1970) / 10000L;
    }
}
