package com.tw0far.potiongames.util;

/**
 * Compares plugin version strings such as {@code 1.0.0-dev.20} or {@code 7.0.1}.
 *
 * <p>Numbers are compared numerically, a missing pre-release qualifier beats a
 * present one ({@code 1.0.0} &gt; {@code 1.0.0-dev.9}), and qualifiers are compared
 * token by token so {@code dev.20} is newer than {@code dev.9}.</p>
 */
public final class PluginVersion {

    private PluginVersion() {
    }

    /** Negative when {@code a} is older than {@code b}, zero when equal, positive when newer. */
    public static int compare(String a, String b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        String[] left = split(a);
        String[] right = split(b);
        int core = compareTokens(left[0], right[0]);
        if (core != 0) {
            return core;
        }
        boolean leftReleased = left[1].isEmpty();
        boolean rightReleased = right[1].isEmpty();
        if (leftReleased && rightReleased) {
            return 0;
        }
        if (leftReleased) {
            return 1;
        }
        if (rightReleased) {
            return -1;
        }
        return compareTokens(left[1], right[1]);
    }

    /** True when {@code candidate} is strictly newer than {@code current}. */
    public static boolean isNewer(String candidate, String current) {
        return compare(candidate, current) > 0;
    }

    private static String[] split(String version) {
        String trimmed = version.trim();
        int dash = trimmed.indexOf('-');
        if (dash < 0) {
            return new String[]{trimmed, ""};
        }
        return new String[]{trimmed.substring(0, dash), trimmed.substring(dash + 1)};
    }

    private static int compareTokens(String a, String b) {
        String[] left = a.isEmpty() ? new String[0] : a.split("\\.");
        String[] right = b.isEmpty() ? new String[0] : b.split("\\.");
        int length = Math.max(left.length, right.length);
        for (int i = 0; i < length; i++) {
            String l = i < left.length ? left[i] : "0";
            String r = i < right.length ? right[i] : "0";
            int result;
            if (isNumeric(l) && isNumeric(r)) {
                result = Long.compare(Long.parseLong(l), Long.parseLong(r));
            } else {
                result = Integer.signum(l.compareToIgnoreCase(r));
            }
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private static boolean isNumeric(String token) {
        if (token.isEmpty()) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
