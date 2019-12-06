package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.Objects;

public final class RedmineNewIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        logger.debug("Starting creating case object for issue id {}, user id {}, company id {}",
                 issue.getId(), user.getId(), companyId);
        final CaseObject object = createCaseObject(user, issue, endpoint);
        if (object == null)
            logger.debug("Object was not created");
        else
            logger.debug("Object with id {} was created, guid={}", object.getId(), object.defGUID());
    }

    private CaseObject createCaseObject(User user, Issue issue, RedmineEndpoint endpoint) {
        logger.debug("Creating case object ...");
        final long companyId = endpoint.getCompanyId();
        final CaseObject testExists = caseObjectDAO.getByExternalAppCaseId(issue.getId().toString()
                + "_" + companyId );
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
        final ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(issue.getId() + "_" + companyId);
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        logger.debug("create redmine-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);

        commonService.processAttachments(issue, obj, contactPerson.getId(), endpoint);

        handleComments(issue, contactPerson.getId(), obj, caseObjId, companyId);

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

        logger.debug("Trying to get portal priority level id matching with redmine: {}", issue.getPriorityId());
        String redminePriorityName = issue.getCustomFieldById(RedmineUtils.REDMINE_CUSTOM_FIELD_ID).getValue();
        if (redminePriorityName == null || redminePriorityName.isEmpty())
            redminePriorityName = RedmineUtils.REDMINE_BASIC_PRIORITY;
        final RedminePriorityMapEntry redminePriorityMapEntry =
                priorityMapEntryDAO.getByRedminePriorityName(redminePriorityName, priorityMapId);
        if (redminePriorityMapEntry != null) {
            logger.debug("Found priority level id: {}", redminePriorityMapEntry.getLocalPriorityId());
            obj.setImpLevel(redminePriorityMapEntry.getLocalPriorityId());
        } else
            logger.debug("Priority level not found, setting default");


        logger.debug("Trying to get portal status id matching with redmine: {}", issue.getStatusId());
        final RedmineToCrmEntry redmineStatusMapEntry =
                statusMapEntryDAO.getLocalStatus(statusMapId, issue.getStatusId());
        if (redmineStatusMapEntry != null) {
            logger.debug("Found status id: {}", redmineStatusMapEntry.getLocalStatusId());
            obj.setStateId(redmineStatusMapEntry.getLocalStatusId());
        } else {
            logger.debug("Object status was not found");
            obj.setStateId(1);
        }

        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(companyId);
        return obj;
    }

    private void handleComments(Issue issue, Long personId, CaseObject obj, long caseObjId, long companyId) {
        logger.debug("Processing comments ...");

        issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getNotes() != null)
                .filter(x -> !x.getNotes().isEmpty())
                .map(x -> commonService.parseJournal(x, companyId))
                .filter(Objects::nonNull)
                .forEach(x -> commonService.processStoreComment(issue, personId, obj, caseObjId, x));
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

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);
}
