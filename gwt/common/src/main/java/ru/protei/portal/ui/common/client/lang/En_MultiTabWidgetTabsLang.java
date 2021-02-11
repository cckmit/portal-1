package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_MultiTabWidgetTabs;

public class En_MultiTabWidgetTabsLang {
    public String getName(En_MultiTabWidgetTabs type) {
        switch (type) {
            case COMMENT: return lang.comments();
            case HISTORY: return lang.caseHistory();
            default: return "";
        }
    }

    @Inject
    Lang lang;
}
