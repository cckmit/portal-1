package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseObjectCreateEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.redmine.service.CommonService;

import java.util.Objects;

public class RedmineNewIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        logger.debug("Starting creating case object for issue id {}, user id {}, company id {}", issue.getId(), user.getId(), companyId);
        final CaseObject object = createCaseObject(user, issue, endpoint);
        if (object == null) {
            logger.debug("Object was not created");
        } else {
            logger.debug("Object with id {} was created, guid={}", object.getId(), object.defGUID());
        }
    }

    private CaseObject createCaseObject(User user, Issue issue, RedmineEndpoint endpoint) {
        logger.debug("Creating case object ...");
        final long companyId = endpoint.getCompanyId();
        final CaseObject testExists = caseObjectDAO.getByExternalAppCaseId(issue.getId().toString() + "_" + companyId );
        if (testExists != null) {
            logger.debug("issue {} is already created as case-object {}", issue.getId(), testExists.defGUID());
            return testExists;
        }

        Person contactPerson = commonService.getAssignedPerson(companyId, user);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id {} from project with id", issue.getId(), issue.getProjectId());
            return null;
        }

        final CaseObject obj = buildCaseObject(issue, contactPerson, endpoint);
        final long caseObjId = caseObjectDAO.insertCase(obj);

        Long stateCommentId = commonService.createAndStoreStateComment(issue.getCreatedOn(), contactPerson.getId(), obj.getStateId(), caseObjId);
        if (stateCommentId == null) {
            logger.error("State comment for the issue {} not saved!", caseObjId);
        }

        Long importanceCommentId = commonService.createAndStoreImportanceComment(issue.getCreatedOn(), contactPerson.getId(), obj.getImpLevel(), caseObjId);
        if (importanceCommentId == null) {
            logger.error("Importance comment for the issue {} not saved!", caseObjId);
        }

        final ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(issue.getId() + "_" + companyId);
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        logger.debug("create redmine-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());
        externalCaseAppDAO.merge(appData);

        publisherService.publishEvent(new CaseObjectCreateEvent(this, ServiceModule.REDMINE, contactPerson.getId(), obj));

        commonService.processAttachments(issue, obj, endpoint);

        handleComments(issue, caseObjId, companyId);

        return obj;
    }

    private CaseObject buildCaseObject(Issue issue, Person contactPerson, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        final long priorityMapId = endpoint.getPriorityMapId();
        final long statusMapId = endpoint.getStatusMapId();
        final CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
        obj.setCreator(contactPerson);
        obj.setInitiator(contactPerson);
        obj.setCaseType(En_CaseType.CRM_SUPPORT);
        obj.setExtAppType(En_ExtAppType.REDMINE.getCode());

        logger.debug("Trying to get portal priority level id matching with redmine {}", issue.getPriorityId());
        final RedminePriorityMapEntry redminePriorityMapEntry = priorityMapEntryDAO.getByRedminePriorityId(issue.getPriorityId(), priorityMapId);
        if (redminePriorityMapEntry != null) {
            obj.setImpLevel(redminePriorityMapEntry.getLocalPriorityId());
        } else {
            logger.warn( "Priority level not found, setting default" );
            obj.setImpLevel(En_ImportanceLevel.BASIC.getId());
        }

        logger.debug("Trying to get portal status id matching with redmine {}", issue.getStatusId());
        final RedmineToCrmEntry redmineStatusMapEntry = statusMapEntryDAO.getLocalStatus(statusMapId, issue.getStatusId());
        if (redmineStatusMapEntry != null) {
            obj.setStateId(redmineStatusMapEntry.getLocalStatusId());
        } else {
            logger.warn("Object status was not found, setting default");
            obj.setStateId(En_CaseState.CREATED.getId());
        }

        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(companyId);
        return obj;
    }

    private void handleComments(Issue issue, long caseObjId, long companyId) {
        logger.debug("Processing comments ...");

        issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> StringUtils.isNotEmpty(x.getNotes()))
                .map(x -> commonService.parseJournalToCaseComment(x, companyId))
                .filter(Objects::nonNull)
                .forEach(x -> commonService.processStoreComment(x.getAuthor().getId(), caseObjId, x));
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
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);
}
