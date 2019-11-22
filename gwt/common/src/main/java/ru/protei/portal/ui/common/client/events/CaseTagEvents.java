package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;

public class CaseTagEvents {

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

        public Show withEditEnabled(boolean isEditEnabled) {
            this.isEditTagEnabled = isEditEnabled;
            return this;
        }

        public Show withAddEnabled(boolean isAddEnabled) {
            this.isAddNewTagEnabled = isAddEnabled;
            return this;
        }

        public Show readOnly() {
            this.isEnabledAttachOptions = false;
            return this;
        }

        public HasWidgets parent;
        public Long caseId;
        public En_CaseType caseType;
        public boolean isEnabledAttachOptions = true;
        public boolean isEditTagEnabled = false;
        public boolean isAddNewTagEnabled = false;
    }

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

    public static class Remove {
        private CaseTag caseTag;

        public Remove(CaseTag caseTag) {
            this.caseTag = caseTag;
        }

        public CaseTag getCaseTag() {
            return caseTag;
        }
    }
}
