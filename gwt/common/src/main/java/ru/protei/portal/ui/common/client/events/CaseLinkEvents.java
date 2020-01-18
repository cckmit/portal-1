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
        public Removed(Long caseId, CaseLink caseLink, String pageId) {
            this.caseId = caseId;
            this.caseLink = caseLink;
            this.pageId = pageId;
        }

        public CaseLink caseLink;
        public Long caseId;
        public String pageId;
    }

    public static class Added {
        public Added(Long caseId, CaseLink caseLink, String pageId) {
            this.caseId = caseId;
            this.caseLink = caseLink;
            this.pageId = pageId;
        }

        public Long caseId;
        public CaseLink caseLink;
        public String pageId;
    }

    public static class ShowLinkSelector {
        public ShowLinkSelector() {}

        public ShowLinkSelector(IsWidget target) {
            this(target, "");
        }

        public ShowLinkSelector(IsWidget target, String pageId) {
            this(target, pageId, true);
        }

        public ShowLinkSelector(IsWidget target, String pageId, boolean createCrossLinks) {
            this.target = target;
            this.pageId = pageId;
            this.createCrossLinks = createCrossLinks;
        }

        public IsWidget target;
        public String pageId = "";
        public boolean createCrossLinks = true;
    }
}
