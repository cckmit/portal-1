package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.dict.En_CaseType;

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
}
