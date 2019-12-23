package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;

public class CaseLinkEvents {

    public static class Show {

        public Show() {}

        public Show(HasWidgets parent) {
            this.parent = parent;
        }

        public Show withCaseType(En_CaseType caseType) {
            this.caseType = caseType;
            return this;
        }

        public Show withCaseId(Long caseId) {
            this.caseId = caseId;
            return this;
        }

        public Show withReadOnly(boolean isReadOnly) {
            this.isEnabled = !isReadOnly;
            return this;
        }

        public Show readOnly() {
            return withReadOnly(true);
        }

        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
        public boolean isEnabled = true;
    }

    public static class Removed {
        public Removed(Long caseId, CaseLink caseLink) {
            this.caseId = caseId;
            this.caseLink = caseLink;
        }

        public CaseLink caseLink;
        public Long caseId;
    }

    public static class Added {
        public Added(Long caseId, CaseLink caseLink) {
            this.caseId = caseId;
            this.caseLink = caseLink;
        }

        public Long caseId;
        public CaseLink caseLink;
    }
}
