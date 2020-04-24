package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;

public class CaseLinkEvents {

    private static final String PAGE_ID_DEFAULT_VALUE = "";

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

        public Show withPageId(String pageId){
            this.pageId = pageId;
            return this;
        }

        public Show readOnly() {
            return withReadOnly(true);
        }

        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
        public boolean isEnabled = true;
        public String pageId = PAGE_ID_DEFAULT_VALUE;
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
            this(target, caseType, true);
        }

        public ShowLinkSelector(IsWidget target, En_CaseType caseType, boolean createCrossLinks) {
            this.target = target;
            this.caseType = caseType;
            this.createCrossLinks = createCrossLinks;
        }

        public IsWidget target;
        public En_CaseType caseType;
        public boolean createCrossLinks = true;
    }
}
