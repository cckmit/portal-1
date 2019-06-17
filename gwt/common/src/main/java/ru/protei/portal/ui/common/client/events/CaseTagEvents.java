package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Company;

public class CaseTagEvents {

    public static class Create {
        private En_CaseType caseType;
        public Create () {}
        public Create(En_CaseType caseType) {
            this.caseType = caseType;
        }
        public En_CaseType getCaseType() {
            return caseType;
        }
    }

    public static class ChangeCompany {
        private Company company;
        public ChangeCompany () {}
        public ChangeCompany(Company company) {
            this.company = company;
        }
        public Company getCompany() {
            return company;
        }
    }

    public static class ChangeModel {}
}
