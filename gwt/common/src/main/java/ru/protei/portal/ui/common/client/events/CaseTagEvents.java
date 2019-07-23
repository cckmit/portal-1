package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;

public class CaseTagEvents {

    public static class Update {
        private En_CaseType caseType;
        private Company company;
        private CaseTag caseTag;
        private boolean isCompanySelectorVisible;
        public Update() {}
        public Update(En_CaseType caseType, boolean isCompanySelectorVisible) {
            this.caseType = caseType;
            this.isCompanySelectorVisible = isCompanySelectorVisible;
        }
        public Update(En_CaseType caseType, Company company, boolean isCompanySelectorVisible) {
            this.caseType = caseType;
            this.company = company;
            this.isCompanySelectorVisible = isCompanySelectorVisible;
        }
        public Update(CaseTag caseTag, boolean isCompanySelectorVisible) {
            this.caseTag = caseTag;
            this.isCompanySelectorVisible = isCompanySelectorVisible;
        }
        public En_CaseType getCaseType() {
            return caseType;
        }
        public Company getCompany() {
            return company;
        }
        public String getTagName() {
            return caseTag != null ? caseTag.getName() : "";
        }
        public String getTagColor() {
            return caseTag != null ? caseTag.getColor() : "";
        }
        public CaseTag getCaseTag() {
            return caseTag;
        }
        public boolean isCompanyPanelVisible() {
            return isCompanySelectorVisible;
        }
    }

    public static class ChangeModel {}
}
