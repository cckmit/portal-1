package ru.protei.portal.core.service.template.htmldiff;

import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    
    private static Pattern openingTagRegex = Pattern.compile("^\\s*<[^>]+>\\s*$");
    private static Pattern closingTagTexRegex = Pattern.compile("^\\s*</[^>]+>\\s*$");
    private static Pattern tagWordRegex = Pattern.compile("<[^\\s>]+");
    private static Pattern whitespaceRegex = Pattern.compile("^(\\s|&nbsp;)+$");
    private static Pattern wordRegex = Pattern.compile("[\\w#@]+");

    private static String[] specialCaseWordTags = { "<img" };

    public static boolean isTag(String item) {
        if (Arrays.stream(specialCaseWordTags).anyMatch(re -> item != null && item.startsWith(re))) {
            return false;
        }
        return isOpeningTag(item) || isClosingTag(item);
    }

    private static boolean isOpeningTag(String item) {
        return openingTagRegex.matcher(item).matches();
    }

    private static boolean isClosingTag(String item) {
        return closingTagTexRegex.matcher(item).matches();
    }

    public static String stripTagAttributes(String word) {
        Matcher m = tagWordRegex.matcher(word);
        if (!m.find()) {
            return word;
        }
        String tag = m.group();
        word = tag + (word.endsWith("/>") ? "/>" : ">");
        return word;
    }

    public static String wrapText(String text, String tagName, String style) {
        if (StringUtils.isBlank(style)) {
            return String.format("<%s>%s</%s>", tagName, text, tagName);
        } else {
            return String.format("<%s style=\"%s\">%s</%s>", tagName, style, text, tagName);
        }
    }

    public static boolean isStartOfTag(char val) {
        return val == '<';
    }

    public static boolean isEndOfTag(char val) {
        return val == '>';
    }

    public static boolean isStartOfEntity(char val) {
        return val == '&';
    }

    public static boolean isEndOfEntity(char val) {
        return val == ';';
    }

    public static boolean isWhiteSpace(String value) {
        return whitespaceRegex.matcher(value).matches();
    }

    public static boolean isWhiteSpace(char value) {
        return isWhiteSpace(String.valueOf(value));
    }

    public static String stripAnyAttributes(String word) {
        if (isTag(word)) {
            return stripTagAttributes(word);
        }
        return word;
    }

    public static boolean isWord(char text) {
        return wordRegex.matcher(String.valueOf(text)).matches();
    }
}
