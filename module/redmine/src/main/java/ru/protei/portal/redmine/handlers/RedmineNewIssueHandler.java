package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.dao.RedmineStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.service.CommonService;

import java.util.Objects;

public final class RedmineNewIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        logger.debug("Starting creating case object for issue id {}, user id {}, company id {}",
                user.getId(), issue.getId(), companyId);
        final CaseObject object = createCaseObject(user, issue, endpoint);
        if (object == null)
            logger.debug("Object was not created");
        else
            logger.debug("Object with id {} was created", object.getId());
    }

    private CaseObject createCaseObject(User user, Issue issue, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        if (caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId()) != null) {
            return caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId());
        }

        Person contactPerson = commonService.getAssignedPerson(companyId, user);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id {} from project with id", issue.getId(), issue.getProjectId());
            return null;
        }

        final CaseObject obj = buildCaseObject(issue, contactPerson, endpoint);
        final long caseObjId = caseObjectDAO.insertCase(obj);
        final ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(String.valueOf(issue.getId()));
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        logger.debug("create redmine-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);

        commonService.processAttachments(issue, obj, contactPerson);

        handleComments(issue, contactPerson, obj, caseObjId, companyId);

        return obj;
    }

    private CaseObject buildCaseObject(Issue issue, Person contactPerson, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        final long priorityMapId = endpoint.getPriorityMapId();
        final long statusMapId = endpoint.getStatusMapId();
        final CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
        obj.setInitiator(contactPerson);
        obj.setCaseType(En_CaseType.CRM_SUPPORT);

        logger.debug("Trying to get portal priority level id matching with redmine: {}", issue.getPriorityId());
        final int priorityLevelId = priorityMapEntryDAO.getByRedminePriorityId(issue.getPriorityId(), priorityMapId).getLocalPriorityId();
        logger.debug("Found priority level id: {}", priorityLevelId);
        obj.setImpLevel(priorityLevelId);

        logger.debug("Trying to get portal status id matching with redmine: {}", issue.getStatusId());
        final long stateId = statusMapEntryDAO.getByRedmineStatusId(issue.getStatusId(), statusMapId).getLocalStatusId();
        logger.debug("Found status id: {}", stateId);
        obj.setStateId(stateId);

        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(companyId);
        return obj;
    }

    private void handleComments(Issue issue, Person person, CaseObject obj, long caseObjId, long companyId) {
        issue.getJournals()
                .stream()
                .filter(x -> !x.getNotes().isEmpty())
                .map(x -> commonService.parseJournal(x, companyId))
                .filter(Objects::nonNull)
                .forEach(x -> commonService.processStoreComment(issue, person, obj, caseObjId, x));
    }

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineStatusMapEntryDAO statusMapEntryDAO;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);
}
