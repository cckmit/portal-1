package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.CaseObject;

public class CaseTextMarkupUtil {

    public static En_TextMarkup recognizeTextMarkup(CaseObject caseObject) {

        if (caseObject == null) {
            return En_TextMarkup.MARKDOWN;
        }

        if (En_ExtAppType.JIRA.getCode().equals(caseObject.getExtAppType())) {
            return En_TextMarkup.JIRA_WIKI_MARKUP;
        }

        return En_TextMarkup.MARKDOWN;
    }
}
