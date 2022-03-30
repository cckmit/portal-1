package ru.protei.portal.core.service.autoclosecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.CaseObjectClosedNotificationEvent;
import ru.protei.portal.core.event.CaseObjectDeadlineExpireEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class AutoCloseCaseServiceImpl implements AutoCloseCaseService {

    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseCommentService caseCommentService;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private static final Logger log = LoggerFactory.getLogger(AutoCloseCaseServiceImpl.class);

    ResourceBundle langRu = ResourceBundle.getBundle("Lang", new Locale( "ru", "RU"));

    @Override
    @Transactional
    public void processAutoCloseByDeadLine() {
        List<CaseObject> caseObjects = caseObjectDAO.getCases(getCaseQuery());
        for (CaseObject caseObject : CollectionUtils.emptyIfNull(caseObjects)) {
            LocalDate deadline = new Date(caseObject.getDeadline()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            if (deadline.isEqual(today)) {
                caseObject.setStateId(CrmConstants.State.VERIFIED);
                caseObject.setDeadline(null);
                if (!caseObjectDAO.partialMerge(caseObject, "STATE", "deadline")) {
                    log.error("Failed to close case object: {}", caseObject);
                    continue;
                }

                Person customer = caseObject.getInitiator();
                Long caseObjectId = caseObject.getId();
                Long caseNumber = caseObject.getCaseNumber();
                jdbcManyRelationsHelper.fill(customer, Person.Fields.CONTACT_ITEMS);

                notifyCustomerThatCaseIsClosed(customer, caseObjectId, caseNumber);
                addCaseComment(caseObjectId, getLangFor("send_reminder_about_case_object_close"));


                log.info("Case object: {} successfully close", caseObject);
            }
        }
    }

    @Override
    @Transactional
    public void notifyAboutDeadlineExpire() {
        List<CaseObject> caseObjects = caseObjectDAO.getCases(getCaseQuery());
        for (CaseObject caseObject : caseObjects) {
            LocalDate deadline = new Date(caseObject.getDeadline()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            if (deadline.isEqual(today.plusDays(1)) || deadline.isEqual(today.plusDays(5)) || deadline.isEqual(today.plusDays(10))) {
                Person customer = caseObject.getInitiator();
                Long caseObjectId = caseObject.getId();
                Long caseNumber = caseObject.getCaseNumber();
                jdbcManyRelationsHelper.fill(customer, Person.Fields.CONTACT_ITEMS);

                notifyCustomerAboutDeadlineExpire(customer, caseObjectId, caseNumber);
                addCaseComment(caseObjectId, getLangFor("send_reminder_about_deadline_expire"));
            }
        }
    }

    private void addCaseComment(Long caseId, String message) {
        CaseComment comment = new CaseComment(message);
        comment.setCaseId(caseId);
        comment.setOriginalAuthorName(getLangFor("reminder_system_name"));
        comment.setPrivacyType(En_CaseCommentPrivacyType.PUBLIC);
        Result<Long> commentId = caseCommentService.addCommentOnSentReminder(comment);

        if (commentId.isError()) {
            log.warn("addCaseComment(): Can't add case comment about {} for caseId={}",  message, caseId);
        }
    }

    private void notifyCustomerThatCaseIsClosed(Person customer, Long caseObjectId, Long caseNumber) {
        publisherService.publishEvent(new CaseObjectClosedNotificationEvent(this, customer, caseObjectId, caseNumber));
    }

    private void notifyCustomerAboutDeadlineExpire(Person customer, Long caseObjectId, Long caseNumber) {
        publisherService.publishEvent(new CaseObjectDeadlineExpireEvent(this, customer, caseObjectId, caseNumber));
    }

    private CaseQuery getCaseQuery() {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setAutoCLose(true);
        List<Long> stateIds = new ArrayList<>();
        stateIds.add(CrmConstants.State.TEST_CUST);
        caseQuery.setStateIds(stateIds);
        return caseQuery;
    }

    private String getLangFor(String key){
        return langRu.getString( key );
    }
}
