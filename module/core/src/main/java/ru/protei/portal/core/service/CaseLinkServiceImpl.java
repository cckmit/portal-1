package ru.protei.portal.core.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseLinksEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.winter.core.utils.services.lock.LockService;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class CaseLinkServiceImpl implements CaseLinkService {

    private static Logger log = LoggerFactory.getLogger(CaseLinkServiceImpl.class);

    @Autowired
    private CaseLinkDAO caseLinkDAO;
    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private PolicyService policyService;
    @Autowired
    private AuthService authService;
    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackService youtrackService;
    @Autowired
    private EventPublisherService publisherService;

    @Autowired
    private LockService lockService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Result<Map<En_CaseLink, String>> getLinkMap() {
        Map<En_CaseLink, String> linkMap = new HashMap<>();
        linkMap.put(En_CaseLink.CRM, portalConfig.data().getCaseLinkConfig().getLinkCrm());
        linkMap.put(En_CaseLink.YT, portalConfig.data().getCaseLinkConfig().getLinkYouTrack());
        return ok(linkMap);
    }

    @Override
    public Result<List<CaseLink>> getLinks( AuthToken token, Long caseId) {
        if ( caseId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseId, isShowOnlyPrivateLinks(token)));
        return ok(caseLinks);
    }

    @Override
    public Result<YouTrackIssueInfo> getYoutrackIssueInfo(AuthToken authToken, String ytId ) {
        return youtrackService.getIssueInfo( ytId );
    }

    @Override
    @Transactional
    public Result<Long> addYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId ) {
        Long caseId = caseObjectDAO.getCaseIdByNumber(caseNumber);

        return  getYoutrackLinks(caseId).flatMap( caseLinks ->
                findCaseLinkByRemoteId( caseLinks, youtrackId ) ).map(
                CaseLink::getCaseId ).orElseGet( ignore ->
                addCaseLinkOnToYoutrack( caseId, youtrackId ).flatMap( addedLink ->
                        sendNotificationLinkAdded( authToken, caseId, addedLink )
                )
        );
    }

    @Override
    @Transactional
    public Result<Long> removeYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId ) {
        Long caseId = caseObjectDAO.getCaseIdByNumber(caseNumber);

        return getYoutrackLinks(caseId).flatMap( caseLinks ->
                findCaseLinkByRemoteId( caseLinks, youtrackId ) ).flatMap(caseLink ->
                removeCaseLinkOnToYoutrack( caseLink ).flatMap( removedLink ->
                        sendNotificationLinkRemoved( authToken, caseId, removedLink )
                )
        );
    }

    // TODO: убрать person!!!
    @Override
    public Result<Long> createLink(AuthToken authToken, Person initiator, CaseLink link) {
        if (link == null || !isValidLink(link)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean isShowOnlyPrivate = isShowOnlyPrivateLinks(authToken);
        // запрещено изменение ссылок вне зоны видимости
        if (isShowOnlyPrivate && link.isPrivate()) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        boolean isAlreadyExist = caseLinkDAO.checkExistLink(link.getType(), link.getRemoteId());
        if (isAlreadyExist) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        return lockService.doWithLockAndTransaction(CaseLink.class, link.getCaseId(), TimeUnit.SECONDS, 5, transactionTemplate, () -> {
            Long createdLinkId = caseLinkDAO.persist(link);
            switch (link.getType()) {
                case CRM:
                    // для crm-линков создаем зеркальные
                    if (!caseLinkDAO.checkExistLink(En_CaseLink.CRM, link.getCaseId().toString())) {
                        CaseLink crossCrmLink = new CaseLink();
                        crossCrmLink.setCaseId(NumberUtils.toLong(link.getRemoteId()));
                        crossCrmLink.setRemoteId(link.getCaseId().toString());
                        crossCrmLink.setType(En_CaseLink.CRM);

                        caseLinkDAO.persist(crossCrmLink);
                    }
                    break;
                case YT:
                    // для YT-линков создаем зеркальные на YT
                    Long caseNumber = caseObjectDAO.getCaseNumberById(link.getCaseId());
                    if (caseNumber == null) {
                        return error(En_ResultStatus.NOT_FOUND);
                    }

                    youtrackService.setIssueCrmNumberIfDifferent(link.getRemoteId(), caseNumber);
            }

            // TODO: push single CaseLinkEvent
//            publisherService.publishEvent( new CaseLinksEvent( this, ServiceModule.GENERAL, initiator, link.getCaseId(), mergedLinks ) );

            return ok(createdLinkId);
        });
    }

    // TODO: убрать person!!!
    @Override
    public Result removeLink(AuthToken authToken, Person initiator, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseLink existedLink = caseLinkDAO.get(id);
        if ( existedLink == null ) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Set<Long> toRemoveIds = new HashSet<>();
        toRemoveIds.add(id);

        switch (existedLink.getType()) {
            case CRM:
                // удаляем зеркальные CRM-линки
                if ( caseLinkDAO.checkExistLink(En_CaseLink.CRM, existedLink.getCaseId().toString())) {
                    toRemoveIds.add(existedLink.getCaseId());
                }
                break;
            case YT:
                Long caseNumber = caseObjectDAO.getCaseNumberById(existedLink.getCaseId());
                if (caseNumber == null) {
                    return error(En_ResultStatus.NOT_FOUND);
                }

                // для YT-линков удаляем зеркальные на YT
                youtrackService.removeIssueCrmNumberIfSame(existedLink.getRemoteId(), caseNumber);
        }

        int removedCount = caseLinkDAO.removeByKeys(toRemoveIds);
        // TODO: push single CaseLinkEvent
//            publisherService.publishEvent( new CaseLinksEvent( this, ServiceModule.GENERAL, initiator, caseId, mergedLinks ) );

        return removedCount == toRemoveIds.size() ? ok() : error(En_ResultStatus.INTERNAL_ERROR);
    }

    private Result<List<CaseLink>> getYoutrackLinks( Long caseId ) {
        if (caseId == null) return error( En_ResultStatus.INCORRECT_PARAMS );
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setCaseId( caseId );
        caseLinkQuery.setType( En_CaseLink.YT );
        return ok(caseLinkDAO.getListByQuery(caseLinkQuery));
    }

    private Result<CaseLink> findCaseLinkByRemoteId(Collection<CaseLink> caseLinks, String youtrackId ) {
        return find( caseLinks, caseLink -> Objects.equals( caseLink.getRemoteId(), youtrackId ) )
                .map( Result::ok )
                .orElse( error( En_ResultStatus.NOT_FOUND ) );
    }

    private Result<CaseLink> addCaseLinkOnToYoutrack( Long caseId, String youtrackId ) {
        CaseLink newLink = new CaseLink();
        newLink.setCaseId( caseId );
        newLink.setType( En_CaseLink.YT );
        newLink.setRemoteId( youtrackId );
        Long id = caseLinkDAO.persist( newLink );
        if (id == null) {
            log.error( "addCaseLinkOnToYoutrack(): Can`t add link on to youtrack into case, persistence error" );
            throw new RollbackTransactionException( "addCaseLinkOnToYoutrack(): rollback transaction" );
        }
        newLink.setId( id );
        return ok( newLink );
    }

    private Result<CaseLink> removeCaseLinkOnToYoutrack( CaseLink caseLink ) {
        if (!caseLinkDAO.removeByKey( caseLink.getId() )) {
            log.error( "removeCaseLinkOnToYoutrack(): Can`t remove link on to youtrack, persistence error" );
            throw new RollbackTransactionException( "removeCaseLinkOnToYoutrack(): rollback transaction" );
        }
        log.info( "removeCaseLinkOnToYoutrack(): removed CaseLink with id={}", caseLink.getId() );
        return ok(caseLink);
    }

    private Result<Long> sendNotificationLinkAdded( AuthToken token, Long caseId, CaseLink caseLInk ) {
        DiffCollectionResult<CaseLink> diff = new DiffCollectionResult<>();
        diff.putAddedEntry( caseLInk );
        return sendNotificationLinkChanged( token, caseId, diff ).map( ignore ->
                caseLInk.getId() );
    }

    private Result<Long> sendNotificationLinkRemoved( AuthToken token, Long caseId, CaseLink caseLInk ) {
        DiffCollectionResult<CaseLink> diff = new DiffCollectionResult<>();
        diff.putRemovedEntry( caseLInk );
        return sendNotificationLinkChanged(token, caseId, diff ).map( ignore ->
                caseLInk.getId() );
    }

    private Result<Void> sendNotificationLinkChanged(AuthToken token, Long caseId, DiffCollectionResult<CaseLink> linksDiff ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        publisherService.publishEvent( new CaseLinksEvent(this, ServiceModule.GENERAL, descriptor.getPerson(), caseId, linksDiff ));
        return ok();
    }

    private boolean isValidLink(CaseLink value) {
        if ( value.getCaseId() == null || value.getType() == null || StringUtils.isBlank(value.getRemoteId())) {
            return false;
        }

        return !En_CaseLink.CRM.equals(value.getType()) || NumberUtils.isDigits(value.getRemoteId());
    }

    private boolean isShowOnlyPrivateLinks(AuthToken token) {
        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        return !policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW);
    }
}
