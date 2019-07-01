package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Company;

public class CaseTagEvents {

    public static class Create {
        private En_CaseType caseType;
        private Company company;
        public Create () {}
        public Create(En_CaseType caseType) {
            this.caseType = caseType;
        }
        public Create(En_CaseType caseType, Company company) {
            this.caseType = caseType;
            this.company = company;
        }
        public En_CaseType getCaseType() {
            return caseType;
        }
        public Company getCompany() {
            return company;
        }
    }

    public static class ChangeModel {}
}
