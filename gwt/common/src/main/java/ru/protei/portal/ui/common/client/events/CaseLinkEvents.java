package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;

import java.util.List;

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

        public Show withLinks(List<CaseLink> links) {
            this.links = links;
            return this;
        }

        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
        public boolean isEnabled = true;
        public List<CaseLink> links;
    }

    public static class Removed {
        public Removed(Long caseId, CaseLink caseLink, En_CaseType caseType) {
            this.caseId = caseId;
            this.caseLink = caseLink;
            this.caseType = caseType;
        }

        public CaseLink caseLink;
        public Long caseId;
        public En_CaseType caseType;
    }

    public static class Added {
        public Added(Long caseId, CaseLink caseLink, En_CaseType caseType) {
            this.caseId = caseId;
            this.caseLink = caseLink;
            this.caseType = caseType;
        }

        public Long caseId;
        public CaseLink caseLink;
        public En_CaseType caseType;
    }

    public static class ShowLinkSelector {
        public ShowLinkSelector() {}

        public ShowLinkSelector(IsWidget target) {
            this(target, null);
        }

        public ShowLinkSelector(IsWidget target, En_CaseType caseType) {
            this.target = target;
            this.caseType = caseType;
        }

        public IsWidget target;
        public En_CaseType caseType;
    }

    public static class Changed {
        public Changed(CaseLink caseLink, En_CaseType caseType) {
            this.caseLink = caseLink;
            this.caseType = caseType;
        }

        public CaseLink caseLink;
        public En_CaseType caseType;
    }
}
