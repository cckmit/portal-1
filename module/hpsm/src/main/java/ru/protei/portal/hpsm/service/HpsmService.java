package ru.protei.portal.hpsm.service;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;

/**
 * Created by michael on 27.04.17.
 */
public interface HpsmService {

//    Company getCompanyByBranchName (String branchName);

    void handleInboundRequest ();

    @EventListener
    void onAssembledCaseEvent(AssembledCaseEvent event);
}
