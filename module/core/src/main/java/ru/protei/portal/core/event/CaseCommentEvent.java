package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.service.CaseService;

/**
 * Created by michael on 04.05.17.
 */
public class CaseCommentEvent extends ApplicationEvent {

    private CaseObject caseObject;
    private CaseComment caseComment;

    public CaseCommentEvent(CaseService source, CaseObject caseObject, CaseComment comment) {
        super(source);
        this.caseObject = caseObject;
        this.caseComment = comment;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public CaseComment getCaseComment() {
        return caseComment;
    }
}
