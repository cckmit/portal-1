package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.CaseTag;

public class CaseTagEvents {

    public static class Update {
        private CaseTag caseTag;
        private boolean isCompanyPanelVisible;

        public Update(CaseTag caseTag, boolean isCompanyPanelVisible) {
            this.caseTag = caseTag;
            this.isCompanyPanelVisible = isCompanyPanelVisible;
        }

        public CaseTag getCaseTag() {
            return caseTag;
        }

        public boolean isCompanyPanelVisible() {
            return isCompanyPanelVisible;
        }
    }

    public static class Readonly {
        private CaseTag caseTag;

        public Readonly(CaseTag caseTag) {
            this.caseTag = caseTag;
        }

        public CaseTag getCaseTag() {
            return caseTag;
        }
    }

    public static class ChangeModel {}
}
