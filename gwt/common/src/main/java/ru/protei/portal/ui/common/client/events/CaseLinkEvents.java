package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;

public class CaseLinkEvents {

    public static class Show {

        public Show() {}

        public Show(HasWidgets parent) {
            this.parent = parent;
        }

        public static class Builder {
            private Show event;
            public Builder(HasWidgets parent) {
                event = new Show(parent);
            }
            public Show.Builder withCaseType(En_CaseType caseType) {
                event.caseType = caseType;
                return this;
            }

            public Show.Builder withCaseId(Long caseId) {
                event.caseId = caseId;
                return this;
            }

            public Show.Builder readOnly() {
                event.isEnabled = false;
                return this;
            }

            public Show build() {
                return event;
            }
        }


        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
        public boolean isEnabled = true;
    }
}
