package ru.protei.portal.core.service.autoclosecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.CaseObjectDeadlineExpireEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_CaseType;
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
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

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
    @Autowired
    PortalConfig config;

    private static final Logger log = LoggerFactory.getLogger(AutoCloseCaseServiceImpl.class);

    @Override
    public void processAutoCloseByDeadLine() {
        CaseQuery query = getCaseQuery();
        query.setOverdueDeadlines(true);
        List<CaseObject> caseObjects = caseObjectDAO.getCases(query);
        AuthToken token = createSystemUserToken();
        for (CaseObject caseObject : CollectionUtils.emptyIfNull(caseObjects)) {
            caseObject.setStateId(CrmConstants.State.DONE);
            caseObject.setDeadline(null);
            caseObject.setAutoClose(false);
            CaseObjectMeta caseObjectMeta = new CaseObjectMeta(caseObject);
            Result<CaseObjectMeta> caseObjectMetaResult = caseService.updateCaseObjectMeta(token, caseObjectMeta);
            if (caseObjectMetaResult.isError()) {
                log.warn("updateCaseObjectMeta(): Can't update case object meta={}", caseObject);
                continue;
            }

            Long caseObjectId = caseObject.getId();
            String locale = caseObject.getInitiator() == null ? null : caseObject.getInitiator().getLocale();
            CaseComment comment = createCaseComment(caseObjectId, getLangFor("issue_was_closed", locale));
            Result<CaseComment> caseCommentResult = caseCommentService.addCaseComment(token, En_CaseType.CRM_SUPPORT, comment);
            if (caseCommentResult.isError()) {
                log.warn("addCaseComment(): Can't add case comment about {} for caseId={}",  comment.getText(), caseObjectId);
            }

            log.info("Issue: {} was successfully closed", caseObject);
        }
    }

    @Override
    public void notifyAboutDeadlineExpire() {
        List<CaseObject> caseObjects = caseObjectDAO.getCases(getCaseQuery());
        LocalDate today = LocalDate.now();
        for (CaseObject caseObject : CollectionUtils.emptyIfNull(caseObjects)) {
            LocalDate deadline = new Date(caseObject.getDeadline()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (deadline.isEqual(today.plusDays(1)) || deadline.isEqual(today.plusDays(5)) || deadline.isEqual(today.plusDays(10))) {
                Long caseObjectId = caseObject.getId();
                Long caseNumber = caseObject.getCaseNumber();
                boolean isPrivateCase = caseObject.isPrivateCase();

                notifyCustomerAboutDeadlineExpire(getNotifiers(caseObject), caseObjectId, caseNumber, isPrivateCase);

                String locale = caseObject.getInitiator() == null ? null : caseObject.getInitiator().getLocale();
                CaseComment comment = createCaseComment(caseObjectId, getLangFor("send_reminder_about_deadline_expire", locale));
                Result<Long> result = caseCommentService.addCommentOnSentReminder(comment);
                if (result.isError()) {
                    log.warn("addCommentOnSentReminder(): Can't add case comment about {} for caseId={}",  comment.getText(), caseObjectId);
                }
            }
        }
    }

    private CaseComment createCaseComment(Long caseId, String message) {
        CaseComment comment = new CaseComment(message);
        comment.setCaseId(caseId);
        comment.setAuthorId(config.data().getCommonConfig().getSystemUserId());
        comment.setPrivacyType(En_CaseCommentPrivacyType.PUBLIC);
        return comment;
    }

    private void notifyCustomerAboutDeadlineExpire(Set<Person> notifiers, Long caseObjectId, Long caseNumber, boolean isPrivateCase) {
        publisherService.publishEvent(new CaseObjectDeadlineExpireEvent(this, notifiers, caseObjectId, caseNumber, isPrivateCase));
    }

    private CaseQuery getCaseQuery() {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setAutoClose(true);
        List<Long> stateIds = new ArrayList<>();
        stateIds.add(CrmConstants.State.TEST_CUST);
        caseQuery.setStateIds(stateIds);
        caseQuery.setOnlyNotExternal(true);
        return caseQuery;
    }

    private AuthToken createSystemUserToken() {
        AuthToken token = new AuthToken("0");
        try {
            token.setIp( Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            token.setIp("0.0.0.0");
        }
        Long systemUserId = config.data().getCommonConfig().getSystemUserId();
        token.setPersonId(systemUserId);
        Set<UserRole> defaultRoles = userRoleDAO.getDefaultManagerRoles();
        token.setRoles(defaultRoles);
        return token;
    }

    private String getLangFor(String key, String locale) {
        if (locale == null) {
            locale = "ru";
        }
        ResourceBundle lang = ResourceBundle.getBundle("Lang", new Locale(locale));
        return lang.getString( key );
    }

    private Set<Person> getNotifiers(CaseObject caseObject) {
        Set<Person> notifiers = stream(new ArrayList<Person>() {{
            add(caseObject.getCreator());
            add(caseObject.getInitiator());
        }}).filter(Objects::nonNull).collect(Collectors.toSet());
        jdbcManyRelationsHelper.fill(notifiers, Person.Fields.CONTACT_ITEMS);
        return notifiers;
    }
}
