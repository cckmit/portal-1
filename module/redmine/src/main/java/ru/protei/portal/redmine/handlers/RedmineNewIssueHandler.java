package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.redmine.api.RedmineIssueType;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.RedmineUtils;

public class RedmineNewIssueHandler implements RedmineEventHandler {
    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private CommonService commonService;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);

    @Override
    public void handle(User user, Issue issue, long companyId) {
        CaseObject object = createCaseObject(user, issue, companyId);
    }

    private CaseObject createCaseObject(User user, Issue issue, long companyId) {
        Person contactPerson = commonService.getAssignedPerson(companyId, user, issue);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id {} from project with id", issue.getId(), issue.getProjectId());
            return null;
        }
        CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
        obj.setCaseType(En_CaseType.CRM_SUPPORT);
        //obj.setProduct(product);
        obj.setInitiator(contactPerson);
        obj.setImpLevel(issue.getPriorityId());
        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(companyId);
        obj.setStateId(En_CaseState.CREATED.getId());
        long caseObjId = caseObjectDAO.insertCase(obj);
        handleComments(issue, contactPerson, obj, caseObjId);
        return obj;
    }

    private void handleComments(Issue issue, Person person, CaseObject obj, long caseObjId) {
        issue.getJournals()
                .stream()
                .map(RedmineUtils::parseJournal)
                .map(x -> commonService.processStoreComment(issue, person, obj, caseObjId, x))
                .forEach(caseCommentDAO::saveOrUpdate);
    }
}
