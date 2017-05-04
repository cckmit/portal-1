package ru.protei.portal.core.event;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.service.CaseService;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectAddCommentEvent extends CaseObjectEvent {

    private CaseComment caseComment;

    public CaseObjectAddCommentEvent(CaseService source, CaseObject object, CaseComment comment) {
        super(source, object);
        this.caseComment = comment;
    }

    public CaseComment getCaseComment() {
        return caseComment;
    }
}
