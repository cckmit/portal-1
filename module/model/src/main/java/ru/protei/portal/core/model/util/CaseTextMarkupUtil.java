package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.CaseObject;

public class CaseTextMarkupUtil {

    private static final String CASE_APP_TYPE_JIRA = "jira";

    public static En_TextMarkup recognizeTextMarkup(CaseObject caseObject) {

        if (caseObject == null) {
            return En_TextMarkup.MARKDOWN;
        }

        if (CASE_APP_TYPE_JIRA.equals(caseObject.getExtAppType())) {
            return En_TextMarkup.JIRA_WIKI_MARKUP;
        }

        return En_TextMarkup.MARKDOWN;
    }
}
