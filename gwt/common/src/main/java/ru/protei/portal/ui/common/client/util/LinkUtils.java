package ru.protei.portal.ui.common.client.util;

import com.google.gwt.user.client.Window;
import ru.protei.portal.core.model.util.CrmConstants;

public class LinkUtils {

    public static String makePreviewLink(Class<?> clazz, Long id) {
        String href = Window.Location.getHref();

        if (id == null) return "";

        switch (clazz.getSimpleName()) {
            case ("Contract"):
                return href.substring(0, href.indexOf("#") + 1) + "contract_preview:id=" + id;
            case ("Project"):
                return href.substring(0, href.indexOf("#") + 1) + "project_preview:id=" + id;
            case ("Platform"):
                return href.substring(0, href.indexOf("#") + 1) + "sfplatform_preview:id=" + id;
            case ("DevUnit"):
                return href.substring(0, href.indexOf("#") + 1) + "product_preview:id=" + id;
            case ("EmployeeShortView"):
                return href.substring(0, href.indexOf("#") + 1) + "employee_preview:id=" + id;
            case ("Plan"):
                return href.substring(0, href.indexOf("#") + 1) + "plan_preview:id=" + id;
            case ("Company"):
                return href.substring(0, href.indexOf("#") + 1) + "company:id=" + id;
            default:
                return "";
        }
    }

    public static String makeEditLink(Class<?> clazz, Long id){
        String href = Window.Location.getHref();

        if (id == null) return "";

        switch (clazz.getSimpleName()) {
            case ("EmployeeShortView"):
                return href + "/employee:id=" + id;
            default:
                return "";
        }
    }

    public static String makeJiraInfoLink() {
        String href = Window.Location.getHref();

        return href.substring(0, href.indexOf("#") + 1) + CrmConstants.Jira.INFO_LINK;
    }

    public static boolean isLinkNeeded(Class<?> clazz) {
        if (clazz == null) return false;

        switch (clazz.getSimpleName()) {
            case ("Contract"):
            case ("Project"):
            case ("Platform"):
            case ("DevUnit"):
            case ("EmployeeShortView"):
            case ("Plan"):
                return true;
            default:
                return false;
        }
    }
}
