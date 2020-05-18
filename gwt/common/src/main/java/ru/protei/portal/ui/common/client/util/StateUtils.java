package ru.protei.portal.ui.common.client.util;

public class StateUtils {
    public static String makeStyleName(String state) {
        return state.replaceAll(" ", "_").toLowerCase();
    }
}
