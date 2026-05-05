package br.com.scmjf.tihelper.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isBlank();
    }

    public static boolean isValidIpv4(String value) {
        return value != null && IPV4_PATTERN.matcher(value.trim()).matches();
    }
}
