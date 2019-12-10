package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;

/**
 * Названия платформ связей обращений
 */
public class En_CaseLinkLang {

    public String getCaseLinkName(En_CaseLink caseLink) {
        switch (caseLink) {
            case CRM: return lang.caseLinkCrm();
            case YT: return lang.caseLinkYouTrack();
            default: return lang.unknownField();
        }
    }

    public String getCaseLinkShortName(En_CaseLink caseLink) {
        switch (caseLink) {
            case CRM: return lang.caseLinkCrmShort();
            case YT: return lang.caseLinkYouTrackShort();
            default: return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
