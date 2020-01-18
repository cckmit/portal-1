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
import ru.protei.portal.core.event.CaseLinkEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.services.lock.LockService;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.find;

public class CaseLinkServiceImpl implements CaseLinkService {

    private static Logger log = LoggerFactory.getLogger(CaseLinkServiceImpl.class);

    @Autowired
    private CaseLinkDAO caseLinkDAO;
    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private PolicyService policyService;
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

        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseId, isShowOnlyPublicLinks(token)));
        return ok(caseLinks);
    }

    @Override
    public Result<YouTrackIssueInfo> getYoutrackIssueInfo(AuthToken authToken, String ytId ) {
        boolean isShowOnlyPublic = isShowOnlyPublicLinks(authToken);
        if (isShowOnlyPublic && En_CaseLink.YT.isForcePrivacy()) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
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

    @Override
    @Transactional
    public Result<Long> createLink(AuthToken authToken, CaseLink link, boolean createCrossLinks) {

        Result<CaseLink> result = validateLinkBeforeAdd(link, authToken);
        if (result.isError())
            return error(result.getStatus());

        Long createdLinkId = addLink(link, createCrossLinks).getData();

        return ok(createdLinkId);
    }

    @Override
    @Transactional
    public Result<Long> createLinkWithPublish(AuthToken authToken, CaseLink link, boolean createCrossLinks) {

        Result<CaseLink> result = validateLinkBeforeAdd(link, authToken);
        if (result.isError())
            return error(result.getStatus());

        Long createdLinkId = addLink(link, createCrossLinks).getData();

        publisherService.publishEvent( new CaseLinkEvent( this, ServiceModule.GENERAL, authToken.getPersonId(), link.getCaseId(), link, null ) );

        return ok(createdLinkId);
    }

    @Override
    @Transactional
    public Result deleteLink (AuthToken authToken, Long id) {
        Result<CaseLink> validationResult = validateLinkBeforeRemove(id);
        if (validationResult.isError())
            return error(validationResult.getStatus());

        return removeLink(validationResult.getData());
    }

    @Override
    @Transactional
    public Result deleteLinkWithPublish (AuthToken authToken, Long id) {
        Result<CaseLink> validationResult = validateLinkBeforeRemove(id);
        if (validationResult.isError())
            return error(validationResult.getStatus());

        CaseLink existedLink = validationResult.getData();

        Result result = removeLink(existedLink);

        publisherService.publishEvent(new CaseLinkEvent(this, ServiceModule.GENERAL, authToken.getPersonId(), existedLink.getCaseId(), null, existedLink));

        return result;
    }


    private Result removeLink (CaseLink link){
        Set<Long> toRemoveIds = new HashSet<>();
        toRemoveIds.add(link.getId());

        switch (link.getType()) {
            case CRM:
                // удаляем зеркальные CRM-линки
                CaseLink mirrorCrmLink = caseLinkDAO.getCrmLink(En_CaseLink.CRM, NumberUtils.toLong(link.getRemoteId()), link.getCaseId().toString());
                if ( mirrorCrmLink != null ) {
                    toRemoveIds.add(mirrorCrmLink.getId());
                }
                break;
            case YT:
                Long caseNumber = caseObjectDAO.getCaseNumberById(link.getCaseId());
                if (caseNumber == null) {
                    return error(En_ResultStatus.NOT_FOUND);
                }

                // для YT-линков удаляем зеркальные на YT
                youtrackService.removeIssueCrmNumberIfSame(link.getRemoteId(), caseNumber);
        }

        int removedCount = caseLinkDAO.removeByKeys(toRemoveIds);
        return removedCount == toRemoveIds.size() ? ok() : error(En_ResultStatus.INTERNAL_ERROR);
    }

    private Result<CaseLink> validateLinkBeforeRemove(Long id){
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseLink existedLink = caseLinkDAO.get(id);
        if ( existedLink == null ) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(existedLink);
    }

    private Result<CaseLink> validateLinkBeforeAdd(CaseLink link, AuthToken authToken){

        if (link == null || !isValidLink(link)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (Objects.equals(link.getRemoteId(), String.valueOf(link.getCaseId()))) {
            return error(En_ResultStatus.NOT_ALLOWED_LINK_ISSUE_TO_ITSELF);
        }
        boolean isShowOnlyPublic = isShowOnlyPublicLinks(authToken);
        // запрещено изменение ссылок вне зоны видимости
        if (isShowOnlyPublic && link.isPrivate()) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        boolean isAlreadyExist = caseLinkDAO.checkExistLink(link.getType(), link.getCaseId(), link.getRemoteId());
        if (isAlreadyExist) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }
        return ok(link);
    }

    private Result<Long> addLink (CaseLink link, boolean createCrossLinks) {

        return lockService.doWithLockAndTransaction(CaseLink.class, link.getCaseId(), TimeUnit.SECONDS, 5, transactionTemplate, () -> {
            Long createdLinkId = caseLinkDAO.persist(link);
            link.setId(createdLinkId);
            if (createCrossLinks) {
                switch (link.getType()) {
                    case CRM:
                        Long remoteId = NumberUtils.toLong(link.getRemoteId());
                        // для crm-линков создаем зеркальные
                        if (!caseLinkDAO.checkExistLink(En_CaseLink.CRM, remoteId, link.getCaseId().toString())) {
                            CaseLink crossCrmLink = new CaseLink();
                            crossCrmLink.setCaseId(remoteId);
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
            }
            return ok(createdLinkId);
        });
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

    private Result<Long> sendNotificationLinkAdded(AuthToken token, Long caseId, CaseLink added ) {
        publisherService.publishEvent( new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, added, null ));
        return ok(added.getId());
    }

    private Result<Long> sendNotificationLinkRemoved(AuthToken token, Long caseId, CaseLink removed ) {
        publisherService.publishEvent( new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, null, removed ));
        return ok(removed.getId());
    }

    private boolean isValidLink(CaseLink value) {
        if ( value.getCaseId() == null || value.getType() == null || StringUtils.isBlank(value.getRemoteId()) ) {
            return false;
        }

        return !En_CaseLink.CRM.equals(value.getType()) || NumberUtils.isDigits(value.getRemoteId());
    }

    private boolean isShowOnlyPublicLinks(AuthToken token) {
        return !policyService.hasGrantAccessFor(token.getRoles(), En_Privilege.ISSUE_VIEW);
    }
}
