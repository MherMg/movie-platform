package am.platform.movie.api.util;

import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mher13.02.94@gmail.com
 */

public class Validator {

    public static final Map<String, String> VALIDATION_PASSWORD;

    private static final String MIN_LENGTH = "8";
    private static final String LOWER_CASE = "[a-z]";
    private static final String UPPER_CASE = "[A-Z]";
    private static final String NUMBER = "[0-9]";

    static {
        VALIDATION_PASSWORD = new LinkedHashMap<>();
        VALIDATION_PASSWORD.put("minLength", MIN_LENGTH);
        VALIDATION_PASSWORD.put("lowerCase", LOWER_CASE);
        VALIDATION_PASSWORD.put("upperCase", UPPER_CASE);
        VALIDATION_PASSWORD.put("number", NUMBER);
    }

    public static final String PASSWORD_PATTERN =
            String.format("^(?=.*%s)(?=.*%s)(?=.*%s).{%s",
                    NUMBER,
                    LOWER_CASE,
                    UPPER_CASE,
                    MIN_LENGTH + ",}$"
            );

    public static final String VALIDATION_EMAIL =
            "^[A-Z0-9._%+-]+@[A-Z0-9/-]+\\.[A-Z]{2,6}$";


    public static boolean isValidPassword(final String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isValidEmail(final String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        Pattern pattern = Pattern.compile(VALIDATION_EMAIL, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
