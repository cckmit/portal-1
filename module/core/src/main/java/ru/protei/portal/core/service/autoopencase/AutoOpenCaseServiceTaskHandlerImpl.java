package ru.protei.portal.core.service.autoopencase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CaseObjectMetaDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.PersonShortViewDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
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
    public void runOpenCaseTask( Long caseId ) {
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

        PersonShortView commonManager = personShortViewDAO.getCommonManagerByProductId(caseMeta.getProductId());
        if (commonManager == null) {
            log.error("No set common manager, case id = {}", caseId);
            return;
        }

        caseMeta.setManager(commonManager);
        caseMeta.setStateId(CrmConstants.State.OPENED);

        caseService.updateCaseObjectMeta(createFakeToken(commonManager), caseMeta);

        log.info("End process case id = {}, manager id = {}", caseId, commonManager.getId());
    }

    private AuthToken createFakeToken( PersonShortView commonManager) {
        AuthToken token = new AuthToken("0");
        try {
            token.setIp( Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            token.setIp("0.0.0.0");
        }
        token.setUserLoginId(0L);
        token.setPersonId(commonManager.getId());
        token.setPersonDisplayShortName(commonManager.getDisplayShortName());
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
    PersonShortViewDAO personShortViewDAO;
    @Autowired
    CaseService caseService;

    @Autowired
    UserRoleDAO userRoleDAO;

    private static Logger log = LoggerFactory.getLogger( AutoOpenCaseServiceTaskHandlerImpl.class );
}
