package ru.protei.portal.core.service.autoclosecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.CaseObjectDeadlineExpireEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.net.Inet4Address;
import java.net.UnknownHostException;
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
    @Autowired
    CaseService caseService;
    @Autowired
    UserRoleDAO userRoleDAO;

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
                CaseObjectMeta caseObjectMeta = new CaseObjectMeta(caseObject);
                caseService.updateCaseObjectMeta(createFakeToken(), caseObjectMeta);

                log.info("Case object: {} successfully close", caseObject);
            }
        }
    }

    @Override
    @Transactional
    public void notifyAboutDeadlineExpire() {
        List<CaseObject> caseObjects = caseObjectDAO.getCases(getCaseQuery());
        for (CaseObject caseObject : CollectionUtils.emptyIfNull(caseObjects)) {
            if (!caseObject.isPrivateCase()) {
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

    private void notifyCustomerAboutDeadlineExpire(Person customer, Long caseObjectId, Long caseNumber) {
        publisherService.publishEvent(new CaseObjectDeadlineExpireEvent(this, customer, caseObjectId, caseNumber));
    }

    private CaseQuery getCaseQuery() {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setAutoClose(true);
        List<Long> stateIds = new ArrayList<>();
        stateIds.add(CrmConstants.State.TEST_CUST);
        caseQuery.setStateIds(stateIds);
        caseQuery.setExtAppType(null);
        return caseQuery;
    }

    private AuthToken createFakeToken() {
        AuthToken token = new AuthToken("0");
        try {
            token.setIp( Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            token.setIp("0.0.0.0");
        }
        token.setUserLoginId(0L);
        token.setPersonId(1L);
        token.setPersonDisplayShortName("коллектив");
        token.setCompanyId(1L);
        Set<UserRole> defaultRoles = userRoleDAO.getDefaultManagerRoles();
        token.setRoles(defaultRoles);
        return token;
    }

    private String getLangFor(String key){
        return langRu.getString( key );
    }
}
