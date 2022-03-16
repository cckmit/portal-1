package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;

public class En_AutoCloseLang {
    public String getName(String value) {
        if ("true".equals(value)) {
            return lang.issueAutoCloseEnabled();
        }
        return lang.issueAutoCloseDisabled();
    }

    @Inject
    Lang lang;
}
