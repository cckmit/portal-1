package ru.protei.portal.core.service.autoopencase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CaseObjectMetaDAO;
import ru.protei.portal.core.model.dao.CommonManagerDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.CaseService;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Set;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

/**
 * Сервис выполняющий автоокрытие помеченных кейсов через определенное время
 */
public class AutoOpenCaseServiceTaskHandlerImpl implements AutoOpenCaseTaskHandler {

    @Async(BACKGROUND_TASKS)
    @Transactional
    @Override
    public void runOpenCaseTaskAsync(Long caseId) {
        runOpenCaseTask(caseId);
    }

    @Override
    public void runOpenCaseTask(Long caseId) {
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

        CommonManager commonManager = commonManagerDAO.getByProductAndCompany(caseMeta.getProductId(), caseMeta.getInitiatorCompany().getId());
        if (commonManager == null) {
            log.error("No set common manager for case id = {},  company id = {}, product id = {},", caseId, caseMeta.getInitiatorCompanyId(), caseMeta.getProduct().getId());
            commonManager = commonManagerDAO.getByProductAndCompany(null, caseMeta.getInitiatorCompany().getId());
        }
        
        if (commonManager == null) {
            log.error("No set common manager for case id = {} company id = {}, ", caseId, caseMeta.getInitiatorCompanyId());
            commonManager = commonManagerDAO.getByProductAndCompany(caseMeta.getProductId(), null);
        }

        if (commonManager == null) {
            log.error("No set common manager, case id = {}", caseId);
            return;
        }

        caseMeta.setManagerId(commonManager.getManagerId());
        caseMeta.setStateId(CrmConstants.State.OPENED);
        caseMeta.setStateName(null);

        caseService.updateCaseObjectMeta(createFakeToken(commonManager), caseMeta);

        log.info("End process case id = {}, manager id = {}", caseId, commonManager.getId());
    }

    private AuthToken createFakeToken( CommonManager commonManager) {
        AuthToken token = new AuthToken("0");
        try {
            token.setIp( Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            token.setIp("0.0.0.0");
        }
        token.setUserLoginId(0L);
        token.setPersonId(commonManager.getManagerId());
        token.setPersonDisplayShortName(commonManager.getManagerName());
        token.setCompanyId(commonManager.getCompanyId());
        token.setCompanyAndChildIds(null);
        Set<UserRole> defaultRoles = userRoleDAO.getDefaultManagerRoles();
        token.setRoles(defaultRoles);

        return token;
    }

    @Autowired
    CaseObjectMetaDAO caseObjectMetaDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CommonManagerDAO commonManagerDAO;
    @Autowired
    CaseService caseService;

    @Autowired
    UserRoleDAO userRoleDAO;

    private static Logger log = LoggerFactory.getLogger( AutoOpenCaseServiceTaskHandlerImpl.class );
}
