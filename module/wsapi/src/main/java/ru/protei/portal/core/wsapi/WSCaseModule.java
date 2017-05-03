package ru.protei.portal.core.wsapi;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;

import javax.jws.WebService;
import java.util.Date;
import java.util.List;

/**
 * Created by Mike on 01.05.2017.
 */
@WebService
public interface WSCaseModule {

    String ping (Date clientTime);

    /**
     * get case object by internal id
     * @param id
     * @return
     */
    CaseObject getCaseObject (long id);

    /**
     * get case object by external id
     * @param extAppId
     * @return
     */
    CaseObject getCaseObjectExtId (String extAppId);

    /**
     * create new case object (support-ticket)
     * @param request
     * @return
     */
    CaseObject createSupportTicket (SupportTicketRequest request);

    /**
     * @param caseId
     * @param comment
     * @return
     */
    CaseComment addComment (long caseId, String comment);

    /**
     *
     * @param caseId
     * @param comment
     * @return
     */
    CaseObject closeCase (long caseId, String comment);


    List<CaseComment> getCaseComments (long caseId);
}
