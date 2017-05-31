package ru.protei.portal.hpsm.service;

import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;

/**
 * Created by michael on 27.04.17.
 */
public interface HpsmService {

//    Company getCompanyByBranchName (String branchName);

    void handleInboundRequest ();

    void onCaseCommentEvent (CaseCommentEvent event);
    void onCaseObjectEvent (CaseObjectEvent event);
}
