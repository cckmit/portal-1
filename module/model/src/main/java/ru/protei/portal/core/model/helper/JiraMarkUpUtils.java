package ru.protei.portal.core.model.helper;

public class JiraMarkUpUtils {
    public static String makeImageString(String fileName, String extLink) {
        return "!" + extLink + "|alt=\"" + fileName +"\"!";
    }
}
