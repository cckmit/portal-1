package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;

public class En_CommentOrHistoryTypeLang {
    public String getName(En_CommentOrHistoryType type) {
        switch (type) {
            case COMMENT: return lang.comments();
            case HISTORY: return lang.caseHistory();
            default: return "";
        }
    }

    @Inject
    Lang lang;
}
