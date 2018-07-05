package ru.protei.portal.ui.issue.client.activity.filter;

import com.google.gwt.storage.client.Storage;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.injector.client.PostConstruct;

public abstract class IssueFilterService implements Activity {

    @PostConstruct
    public void onInit() {
        localStorage = Storage.getLocalStorageIfSupported();
    }

    public Boolean isFilterCollapsed() {
        if (localStorage == null) {
            return null;
        }
        String value = localStorage.getItem(ISSUE_FILTER_COLLAPSED);
        if (value != null && !value.isEmpty() && ISSUE_FILTER_COLLAPSED_TRUE.equals(value)) {
            return true;
        } else {
            return false;
        }
    }

    public void setFilterCollapsed(boolean isCollapsed) {
        if (localStorage != null) {
            localStorage.setItem(ISSUE_FILTER_COLLAPSED, isCollapsed ? ISSUE_FILTER_COLLAPSED_TRUE : ISSUE_FILTER_COLLAPSED_FALSE);
        }
    }

    private Storage localStorage = null;

    private final static String ISSUE_FILTER_COLLAPSED = "issue_filter_collapsed";
    private final static String ISSUE_FILTER_COLLAPSED_TRUE = "1";
    private final static String ISSUE_FILTER_COLLAPSED_FALSE = "0";
}
