package net.jtownson.annotation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class FormatConverter {
    static final String formatSpecifier
            = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";

    static final Pattern formatToken = Pattern.compile(formatSpecifier);

    //public static boolean matches(String expansion)
    public static List<String> split(String format) {
        return asList(formatToken.split(format));
    }

    public static String convert(final String format) {
        final StringBuilder regex = new StringBuilder();
        final Matcher matcher = formatToken.matcher(format);
        int lastIndex = 0;
        regex.append('^');
        while (matcher.find()) {
            regex.append(Pattern.quote(format.substring(lastIndex, matcher.start())));
            regex.append(convertToken(matcher.group(1), matcher.group(2), matcher.group(3),
                    matcher.group(4), matcher.group(5), matcher.group(6)));
            lastIndex = matcher.end();
        }
        regex.append(Pattern.quote(format.substring(lastIndex, format.length())));
        regex.append('$');
        return regex.toString();
    }

    private static String convertToken(String index, String flags, String width, String precision, String temporal, String conversion) {
        return "(.*?)";

    }
}
