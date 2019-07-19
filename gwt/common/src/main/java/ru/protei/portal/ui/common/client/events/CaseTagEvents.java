package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;

public class CaseTagEvents {

    public static class Create {
        private En_CaseType caseType;
        private Company company;
        private CaseTag caseTag;
        public Create () {}
        public Create(En_CaseType caseType) {
            this.caseType = caseType;
        }
        public Create(En_CaseType caseType, Company company) {
            this.caseType = caseType;
            this.company = company;
        }
        public Create(CaseTag caseTag) {
            this.caseTag = caseTag;
            this.company = caseTag.getCompany();
            this.caseType = caseTag.getCaseType();
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
    }

    public static class ChangeModel {}
}
