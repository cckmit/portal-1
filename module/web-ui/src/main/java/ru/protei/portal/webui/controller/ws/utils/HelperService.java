package ru.protei.portal.webui.controller.ws.utils;

import java.text.SimpleDateFormat;

/**
 * Created by turik on 18.08.16.
 */
public class HelperService {

    public static SimpleDateFormat DATE = new SimpleDateFormat ("yyyy-MM-dd");

    public static String generateDisplayName(String firstName, String lastName, String secondName) {
        return lastName + " " + firstName + (secondName != null ? (" " + secondName) : "");
    }

    public static String generateDisplayShortName(String firstName, String lastName, String secondName) {
        return lastName + " " + (firstName.charAt (0) + ".") + (secondName != null ? secondName.charAt(0) + "." : "");
    }

}
