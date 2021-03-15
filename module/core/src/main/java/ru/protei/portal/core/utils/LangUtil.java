package ru.protei.portal.core.utils;

public class LangUtil {

    public static String setLang(String url, String lang) {
        return url.replace("%", lang);
    }
}
