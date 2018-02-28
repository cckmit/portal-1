package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.redmine.api.RedmineIssuePriority;
import ru.protei.portal.redmine.api.RedmineIssueType;
import ru.protei.portal.redmine.api.RedmineStatus;
import ru.protei.portal.redmine.service.CommonService;

import java.util.Objects;

public final class RedmineNewIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, long companyId) {
        createCaseObject(user, issue, companyId);
    }

    private CaseObject buildCaseObject(Issue issue, Person contactPerson, long companyId) {
        final CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
//        obj.setCaseType(RedmineIssueType.find(issue.getTracker().getName()));
        /*DevUnit product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, issue.getProjectName());
        if (product == null) {
            product = new DevUnit(En_DevUnitType.PRODUCT.getId(), issue.getProjectName(), "");
            devUnitDAO.saveOrUpdate(product);
        }
        obj.setProduct(product);*/
        obj.setInitiator(contactPerson);

//        obj.setCaseType(RedmineIssueType.find(issue.getTracker().getId()));
        obj.setCaseType(En_CaseType.CRM_SUPPORT);
        obj.setImpLevel(RedmineIssuePriority.find(issue.getPriorityId()).getCaseImpLevel().getId());
        obj.setState(RedmineStatus.find(issue.getStatusId()).getCaseState());

        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(companyId);
        return obj;
    }

    private CaseObject createCaseObject(User user, Issue issue, long companyId) {
        if (caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId()) != null) {
            return caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId());
        }

        Person contactPerson = commonService.getAssignedPerson(companyId, user);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id {} from project with id", issue.getId(), issue.getProjectId());
            return null;
        }

        final CaseObject obj = buildCaseObject(issue, contactPerson, companyId);
        final long caseObjId = caseObjectDAO.insertCase(obj);
        final ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(String.valueOf(issue.getId()));
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        logger.debug("create hpsm-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);

        commonService.processAttachments(issue, obj, contactPerson);

        handleComments(issue, contactPerson, obj, caseObjId, companyId);

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

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);
}
