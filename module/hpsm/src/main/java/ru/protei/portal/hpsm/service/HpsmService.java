package ru.protei.portal.hpsm.service;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.event.CompleteCaseEvent;

/**
 * Created by michael on 27.04.17.
 */
public interface HpsmService {

//    Company getCompanyByBranchName (String branchName);

    void handleInboundRequest ();

    @EventListener
    void onCompleteCaseEvent(CompleteCaseEvent event);

    /*void onCaseCommentEvent (CaseCommentEvent event);
    void onCaseObjectEvent (CaseObjectEvent event);*/
}
