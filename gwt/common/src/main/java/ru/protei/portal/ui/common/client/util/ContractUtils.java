package ru.protei.portal.ui.common.client.util;

import com.google.gwt.user.client.Window;

public class ContractUtils {

    public static String makeLink(Long id) {
        String href = Window.Location.getHref();
        return href.substring(0, href.indexOf("#") + 1) + "contract_preview:id=" + id;
    }
}
