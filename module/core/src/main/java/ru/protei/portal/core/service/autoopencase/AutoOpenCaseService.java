package ru.protei.portal.core.service.autoopencase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseService;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

/**
 * Сервис выполняющий автоокрытие помеченных кейсов через определенное время
 */
public class AutoOpenCaseService {
    private static Logger log = LoggerFactory.getLogger( AutoOpenCaseService.class );

    @PostConstruct
    public void onStartup() {
        // проверям в базе созданные кейсы, которые надо открыть - ставим задачи на открытие через близашайшее время (до 1-2 минуты)
        List<Long> caseIdToAutoOpen = caseObjectDAO.getCaseIdToAutoOpen();
        caseIdToAutoOpen.forEach(caseId -> createTask(caseId, random, MINUTE));
    }

    public void processNewCreatedCaseToAutoOpen(Long caseId, Long companyId) {
        // при создании кейса ставим задачу на открытие через некоторое время (4-5 минут)
        Company company = companyDAO.get(companyId);
        if (company.getAutoOpenIssue() != null && company.getAutoOpenIssue()) {
            createTask(caseId, random, 4 * MINUTE);
        }
    }


    public ScheduledFuture<?> createTask(Long caseId, Random random, int timeoutOffset) {
        int timeout = timeoutOffset + random.nextInt(60 ) * SEC;
        log.info("schedule case id = {}, timeout = {}", caseId, timeout);
        return scheduler.schedule(() -> runTask(caseId), new Date(new Date().getTime() + timeout));
    }

    public void runTask(Long caseId) {
        log.info("Process case id = {}", caseId);

        CaseObjectMeta caseMeta = caseObjectMetaDAO.get(caseId);
        if (caseMeta.getStateId() != CREATED_STATE) {
            log.info("Already opened case id = {}", caseId);
            return;
        }

        if (caseMeta.getManager() != null) {
            log.info("Already set manager case id = {}", caseId);
            return;
        }

        if (caseMeta.getProductId() == null) {
            log.error("Not set product, case id = {}", caseId);
            return;
        }

        Person commonManager = personDAO.getCommonManagerByProductId(caseMeta.getProductId());
        if (commonManager == null) {
            log.error("No set common manager, case id = {}", caseId);
            return;
        }

        caseMeta.setManager(commonManager);
        caseMeta.setStateId(OPEN_STATE);

        caseService.updateCaseObjectMeta(createToken(commonManager), caseMeta);

        log.info("End process case id = {}, manager id = {}", caseId, commonManager.getId());
    }

    private AuthToken createToken(Person commonManager) {
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
        token.setRoles(defaultEmployeeRoles);

        return token;
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
        defaultEmployeeRoles = userRoleDAO.getDefaultEmployeeRoles();
    }
    Set<UserRole> defaultEmployeeRoles;

    Random random = new Random();

    static public final long CREATED_STATE = 1L;
    static public final long OPEN_STATE = 2L;

    static public final int SEC = 1000;
    static public final int MINUTE = 60 * SEC;
}
