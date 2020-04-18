package ru.protei.portal.core.model.helper;

public class JiraMarkUpUtils {
    public static String makeImageString(String link, String alt) {
        return "!" + link + "|alt=" + alt +"!";
    }
}
