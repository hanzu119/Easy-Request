package com.easy.request.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String TRANSLATE_CHARACTERS = "\\";
    public static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACE = "}";

    private static String baseReplace(String placeholderPatten, String source, Object... objects) {
        int length = objects.length;
        if (length == 0) {
            return source;
        }
        if (isBlank(placeholderPatten)) {
            throw new RuntimeException("placeholder patten can't blank.");
        }
        Pattern pattern = Pattern.compile(placeholderPatten);
        for (Object object : objects) {
            Matcher m = pattern.matcher(source);
            source = m.replaceFirst(String.valueOf(object));
        }
        return source;
    }

    public static String replaceArray(String source, Object... objects) {
        return baseReplace(TRANSLATE_CHARACTERS + LEFT_BRACE + TRANSLATE_CHARACTERS + RIGHT_BRACE, source, objects);
    }

    public static String replacePlaceholder(String placeholder, String source, Object object) {
        String pattenFormat = TRANSLATE_CHARACTERS + LEFT_BRACE;
        if (isNotBlank(placeholder)) {
            pattenFormat += placeholder;
        }
        pattenFormat += TRANSLATE_CHARACTERS + RIGHT_BRACE;
        return baseReplace(pattenFormat, source, object);
    }

}
