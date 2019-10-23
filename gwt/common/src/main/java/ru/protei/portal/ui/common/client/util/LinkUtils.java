package ru.protei.portal.ui.common.client.util;

import com.google.gwt.user.client.Window;

public class LinkUtils {

    public static String makeLink(Class<?> clazz, Long id) {
        String href = Window.Location.getHref();

        if (id == null) return "";

        switch (clazz.getSimpleName()) {
            case ("Contract"):
                return href.substring(0, href.indexOf("#") + 1) + "contract_preview:id=" + id;
            case ("Project"):
                return href.substring(0, href.indexOf("#") + 1) + "project_preview:id=" + id;
            case ("Platform"):
                return href.substring(0, href.indexOf("#") + 1) + "sfplatform_preview:id=" + id;
            default:
                return "";
        }
    }
}
