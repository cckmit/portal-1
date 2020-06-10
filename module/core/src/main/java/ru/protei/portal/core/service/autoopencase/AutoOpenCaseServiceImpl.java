package ru.protei.portal.core.service.autoopencase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.CaseService;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Сервис выполняющий автоокрытие помеченных кейсов через определенное время
 */
public class AutoOpenCaseServiceImpl implements AutoOpenCaseService {

    @PostConstruct
    public void onStartup() {
        log.info("Schedule case open at startup");
        caseObjectDAO.getCaseIdToAutoOpen()
                .forEach(caseId -> createTask(caseId, TimeUnit.MINUTES.toMillis(1) + makeRandomDelaySecond(120)));
    }

    @Override
    public void processNewCreatedCaseToAutoOpen(Long caseId, Long companyId) {
        Company company = companyDAO.get(companyId);
        if (company.getAutoOpenIssue() != null && company.getAutoOpenIssue()) {
            createTask(caseId, TimeUnit.MINUTES.toMillis(3) + makeRandomDelaySecond(120));
        }
    }

    @Override
    public ScheduledFuture<?> createTask(Long caseId, long delay) {
        log.info("Schedule case open id = {}, delay = {}", caseId, delay);
        return scheduler.schedule(() -> runTask(caseId), new Date(new Date().getTime() + delay));
    }

    private void runTask(Long caseId) {
        log.info("Process case id = {}", caseId);

        CaseObjectMeta caseMeta = caseObjectMetaDAO.get(caseId);

        if (caseMeta == null) {
            log.error("No case with id = {}", caseId);
            return;
        }

        if (caseMeta.getStateId() != CrmConstants.State.CREATED) {
            log.info("Already opened case id = {}", caseId);
            return;
        }
        if (caseMeta.getManager() != null) {
            log.info("Already set manager case id = {}", caseId);
            return;
        }

        if (caseMeta.getProductId() == null) {
            log.error("No set product, case id = {}", caseId);
            return;
        }

        Person commonManager = personDAO.getCommonManagerByProductId(caseMeta.getProductId());
        if (commonManager == null) {
            log.error("No set common manager, case id = {}", caseId);
            return;
        }

        caseMeta.setManager(commonManager);
        caseMeta.setStateId(CrmConstants.State.OPENED);

        caseService.updateCaseObjectMeta(createFakeToken(commonManager), caseMeta);

        log.info("End process case id = {}, manager id = {}", caseId, commonManager.getId());
    }

    private AuthToken createFakeToken(Person commonManager) {
        AuthToken token = new AuthToken("0");
        try {
            token.setIp(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            token.setIp("0.0.0.0");
        }
        token.setUserLoginId(0L);
        token.setPersonId(commonManager.getId());
        token.setPersonDisplayShortName(commonManager.getDisplayShortName());
        token.setCompanyId(commonManager.getCompanyId());
        token.setCompanyAndChildIds(null);
        token.setRoles(defaultRoles);

        return token;
    }

    private long makeRandomDelaySecond(int bound) {
        return TimeUnit.SECONDS.toMillis(random.nextInt(bound));
    }

    @Autowired
    CaseService caseService;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseObjectMetaDAO caseObjectMetaDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    ThreadPoolTaskScheduler scheduler;

    @Autowired
    public void setAdminRole(UserRoleDAO userRoleDAO) {
        defaultRoles = userRoleDAO.getDefaultManagerRoles();
    }
    Set<UserRole> defaultRoles;

    private final Random random = new Random();

    private static Logger log = LoggerFactory.getLogger( AutoOpenCaseService.class );
}