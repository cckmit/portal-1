package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
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
        public Removed(Long caseId, CaseLink caseLink, String page) {
            this.caseId = caseId;
            this.caseLink = caseLink;
            this.page = page;
        }

        public CaseLink caseLink;
        public Long caseId;
        public String page;
    }

    public static class Added {
        public Added(Long caseId, CaseLink caseLink, String page) {
            this.caseId = caseId;
            this.caseLink = caseLink;
            this.page = page;
        }

        public Long caseId;
        public CaseLink caseLink;
        public String page;
    }

    public static class ShowLinkSelector {
        public ShowLinkSelector() {}

        public ShowLinkSelector(IsWidget target) {
            this.target = target;
        }

        public ShowLinkSelector(IsWidget target, String page) {
            this.target = target;
            this.page = page;
        }

        public ShowLinkSelector(IsWidget target, String page, boolean withCrossLinks) {
            this.target = target;
            this.page = page;
            this.withCrossLinks = withCrossLinks;
        }

        public IsWidget target;
        public String page;
        public boolean withCrossLinks = true;
    }
}
