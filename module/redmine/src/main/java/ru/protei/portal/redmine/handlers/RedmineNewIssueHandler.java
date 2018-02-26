package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.redmine.api.RedmineIssueType;
import ru.protei.portal.redmine.api.RedmineStatus;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.stream.Collectors;

public final class RedmineNewIssueHandler implements RedmineEventHandler {
    @Autowired
    private DevUnitDAO devUnitDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    private CommonService commonService;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);

    @Override
    public void handle(User user, Issue issue, long companyId) {
        CaseObject object = createCaseObject(user, issue, companyId);
    }

    private CaseObject createCaseObject(User user, Issue issue, long companyId) {
        if (caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId()) != null) {
            return caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId());
        }
        Person contactPerson = commonService.getAssignedPerson(companyId, user, issue);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id {} from project with id", issue.getId(), issue.getProjectId());
            return null;
        }
        CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
        if (issue.getTracker() == null)
            return null;
        obj.setCaseType(En_CaseType.CRM_SUPPORT);
//        obj.setCaseType(RedmineIssueType.find(issue.getTracker().getName()));
        DevUnit product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, issue.getProjectName());
        if (product == null) {
            product = new DevUnit(En_DevUnitType.PRODUCT.getId(), issue.getProjectName(), "");
            devUnitDAO.saveOrUpdate(product);
        }
        obj.setProduct(product);
        obj.setInitiator(contactPerson);
        obj.setImpLevel(issue.getPriorityId());
        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        issue.getProjectName();
        obj.setInitiatorCompanyId(companyId);
        obj.setState(RedmineStatus.parse(issue.getStatusName()).getCaseState());
        long caseObjId = caseObjectDAO.insertCase(obj);
        ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(String.valueOf(issue.getId()));

        logger.debug("create hpsm-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);
        commonService.processAttachments(issue, obj, contactPerson);
        handleComments(issue, contactPerson, obj, caseObjId);
        return obj;
    }

    private void handleComments(Issue issue, Person person, CaseObject obj, long caseObjId) {
        issue.getJournals()
                .stream()
                .filter(x -> !x.getNotes().isEmpty())
                .map(RedmineUtils::parseJournal)
                .forEach(x -> commonService.processStoreComment(issue, person, obj, caseObjId, x));
    }
}
