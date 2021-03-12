package ru.protei.portal.core.model.util;

public class IssueCommentHelpLangUtil {

    public static String setLang(String url, String lang) {
        return url.replace("%", lang);
    }
}
