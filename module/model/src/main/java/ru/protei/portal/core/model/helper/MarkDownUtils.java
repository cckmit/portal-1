package ru.protei.portal.core.model.helper;

public class MarkDownUtils {
    public static String makeImageString(String fileName, String extLink) {
        return "![alt=" + fileName + "](" + extLink + ")";
    }
}
