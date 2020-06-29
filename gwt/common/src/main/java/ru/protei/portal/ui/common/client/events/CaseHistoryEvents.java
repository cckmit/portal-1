package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

public class CaseHistoryEvents {
    public static class Load {
        public Load(Long caseId, HasWidgets container) {
            this.caseId = caseId;
            this.container = container;
        }

        public Long caseId;
        public HasWidgets container;
    }

    public static class Reload {
        public Reload(Long caseId) {
            this.caseId = caseId;
        }

        public Long caseId;
    }
}
