package cput.ac.za.ecommerce.util;

import java.util.Locale;
import java.util.regex.Pattern;

public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    public static final String PERSON_NAME =
            "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$";

    public static final String SOUTH_AFRICAN_MOBILE =
            "^0(?:6\\d|7\\d|8[1-9])\\d{7}$";

    public static final String ALLOWED_EMAIL =
            "^[A-Za-z0-9._%+-]+@(?:"
                    + "gmail\\.com|"
                    + "outlook\\.com|"
                    + "hotmail\\.com|"
                    + "live\\.com|"
                    + "[A-Za-z0-9.-]+\\.co\\.za"
                    + ")$";

    public static final String STRONG_PASSWORD =
            "^(?=.*[a-z])"
                    + "(?=.*[A-Z])"
                    + "(?=.*\\d)"
                    + "(?=.*[^A-Za-z0-9\\s])"
                    + "\\S{12,64}$";

    private static final Pattern NAME_PATTERN =
            Pattern.compile(PERSON_NAME);

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(SOUTH_AFRICAN_MOBILE);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    ALLOWED_EMAIL,
                    Pattern.CASE_INSENSITIVE
            );

    public static boolean isValidName(String value) {
        return value != null
                && value.length() >= 2
                && value.length() <= 50
                && NAME_PATTERN.matcher(value).matches();
    }

    public static boolean isValidPhoneNumber(String value) {
        return value != null
                && PHONE_PATTERN.matcher(value).matches();
    }

    public static boolean isValidEmail(String value) {
        return value != null
                && value.length() <= 254
                && EMAIL_PATTERN.matcher(value).matches();
    }

    public static String normalizeName(String value) {
        return value == null
                ? null
                : value.trim().replaceAll("\\s+", " ");
    }

    public static String normalizeEmail(String value) {
        return value == null
                ? null
                : value.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizePhoneNumber(String value) {
        return value == null
                ? null
                : value.replaceAll("[\\s\\-()]", "");
    }
}