package ru.protei.portal.core.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class CaseLinkServiceImpl implements CaseLinkService {

    private static Logger log = LoggerFactory.getLogger(CaseLinkServiceImpl.class);

    @Autowired
    CaseLinkDAO caseLinkDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    YoutrackService youtrackService;

    @Autowired
    private CaseService caseService;

    @Autowired
    EventPublisherService publisherService;

    @Override
    public Result<Map<En_CaseLink, String>> getLinkMap() {
        Map<En_CaseLink, String> linkMap = new HashMap<>();
        linkMap.put(En_CaseLink.CRM, portalConfig.data().getCaseLinkConfig().getLinkCrm());
        linkMap.put(En_CaseLink.CRM_OLD, portalConfig.data().getCaseLinkConfig().getLinkOldCrm());
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
    @Transactional
    public Result<List<CaseLink>> createLinks(AuthToken token, Long caseId, Person initiator, List<CaseLink> caseLinks) {
        List<CaseLink> allLinks = new ArrayList<>(caseLinks);
        caseLinks.forEach(caseLink -> caseLink.setCaseId(caseId));
        List<String> youtrackLinksRemoteIds = selectYouTrackLinkRemoteIds(caseLinks);
        List<CaseLink> notYoutrackLinks = caseLinks.stream().filter(caseLink -> !youtrackLinksRemoteIds.contains(caseLink.getRemoteId())).collect(Collectors.toList());
        notYoutrackLinks.forEach(caseLink -> allLinks.add(createCrossCRMLink(parseRemoteIdAsLongValue(caseLink.getRemoteId()), caseId)));
        caseLinkDAO.persistBatch(allLinks);
        caseService.getCaseNumberById( token, caseId ).ifOk(caseNumber ->
                youtrackService.mergeYouTrackLinks(caseNumber,
                        youtrackLinksRemoteIds,
                        Collections.emptyList()
                )
        );

        return ok(caseLinks);
    }

    @Override
    @Transactional
    public Result<List<CaseLink>> updateLinks( AuthToken token, Long caseId, Long initiatorId, Collection<CaseLink> caseLinks ) {
        return mergeLinks( token, caseId, caseLinks ).ifOk( mergedLinks -> {
            if (mergedLinks.hasDifferences()) {
                caseService.getCaseNumberById( token, caseId ).ifOk( caseNumber ->
                        youtrackService.mergeYouTrackLinks( caseNumber,
                                selectYouTrackLinkRemoteIds( mergedLinks.getAddedEntries() ),
                                selectYouTrackLinkRemoteIds( mergedLinks.getRemovedEntries() )
                        )
                );
                publisherService.publishEvent( new CaseLinksEvent( this, ServiceModule.GENERAL, initiatorId, caseId, mergedLinks ) );
            }
        } ).flatMap( mergedLinks -> {
            if (!mergedLinks.hasDifferences()) return ok( listOf( caseLinks ) );
            return getLinks( token, caseId ); //TODO оптимизировать из mergedLinks (same + added)
        } );
    }

    private Result<DiffCollectionResult<CaseLink>> mergeLinks( AuthToken token, Long caseId, Collection<CaseLink> caseLinks) {
        if (caseLinks == null) {
            return ok(new DiffCollectionResult());
        }

        caseLinks.forEach(link -> link.setCaseId(caseId));
        if ( caseId == null || !checkLinksIsValid(caseLinks)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean isShowOnlyPrivate = isShowOnlyPrivateLinks(token);
        // запрещено изменение ссылок вне зоны видимости
        if ( isShowOnlyPrivate && caseLinks.stream().anyMatch(CaseLink::isPrivate) ) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        // работаем только с пулом доступных линков по приватности
        List<CaseLink> oldCaseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseId, isShowOnlyPrivate));
        List<CaseLink> oldCaseCrossLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(null, isShowOnlyPrivate, caseId.toString()));
        // линки не могут быть изменены, поэтому удаляем старые и создаем новые. Кросс ссылки добавляем только для новых
        DiffCollectionResult<CaseLink> mergeLinks = diffCollection(oldCaseLinks, caseLinks);
        if ( isNotEmpty(mergeLinks.getRemovedEntries())) {
            Set<Long> toRemoveIds = new HashSet<>();
            mergeLinks.getRemovedEntries().forEach( link -> {
                toRemoveIds.add(link.getId());
                if ( isNotCrmLink(link) ) {
                    return;
                }
                oldCaseCrossLinks.forEach(cl -> {
                    Long cId = parseRemoteIdAsLongValue(link.getRemoteId());
                    if ( Objects.equals(cId, cl.getCaseId())) {
                        toRemoveIds.add(cl.getId());
                    }
                });
            });
            caseLinkDAO.removeByKeys(toRemoveIds);
        }

        if ( isNotEmpty(mergeLinks.getAddedEntries())) {
            List<CaseLink> toAddLinks = new ArrayList<>();
            emptyIfNull(mergeLinks.getAddedEntries()).forEach( link -> {
                if ( isNotCrmLink(link) ) {
                    return;
                }
                Long cId = parseRemoteIdAsLongValue(link.getRemoteId());
                if ( crossLinkAlreadyExist( oldCaseCrossLinks, cId )) {
                    return;
                }
                toAddLinks.add(createCrossCRMLink(cId, caseId));
            });
            toAddLinks.addAll(mergeLinks.getAddedEntries());
            caseLinkDAO.persistBatch(toAddLinks);
        }

        return ok(mergeLinks);
    }

    public Result<List<CaseLink>> getYoutrackLinks( Long caseId ) {
        if (caseId == null) return error( En_ResultStatus.INCORRECT_PARAMS );
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setCaseId( caseId );
        caseLinkQuery.setType( En_CaseLink.YT );
        return ok(caseLinkDAO.getListByQuery(caseLinkQuery));
    }

    @Override
    public Result<YouTrackIssueInfo> getIssueInfo( AuthToken authToken, String ytId ) {
        return youtrackService.getIssueInfo( ytId );
    }

    @Override
    @Transactional
    public Result<Long> addYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId ) {
        Result<Long> caseIdResult = getCaseIdByCaseNumber( authToken, caseNumber );
        Long caseId = caseIdResult.getData();

        return caseIdResult.flatMap(
                this::getYoutrackLinks ).flatMap( caseLinks ->
                findCaseLinkByRemoterId( caseLinks, youtrackId ) ).map(
                CaseLink::getCaseId ).orElseGet( ignore ->
                addCaseLinkOnToYoutrack( caseId, youtrackId ).flatMap( addedLink ->
                        sendNotificationLinkAdded( authToken, caseId, addedLink )
                )
        );
    }

    @Override
    @Transactional
    public Result<Long> removeYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId ) {
        Result<Long> caseIdResult = getCaseIdByCaseNumber( authToken, caseNumber );
        Long caseId = caseIdResult.getData();

        return caseIdResult.flatMap(
                this::getYoutrackLinks).flatMap( caseLinks ->
                findCaseLinkByRemoterId( caseLinks, youtrackId ) ).flatMap( caseLink ->
                removeCaseLinkOnToYoutrack( caseLink ).flatMap( removedLink ->
                        sendNotificationLinkRemoved( authToken, caseId, removedLink )
                )
        );
    }

    private Result<Long> getCaseIdByCaseNumber( AuthToken authToken, Long caseNumber ) {
        return caseService.getCaseIdByNumber( authToken, caseNumber );
    }

    private Result<CaseLink> findCaseLinkByRemoterId( Collection<CaseLink> caseLinks, String youtrackId ) {
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

    public Result<Void> sendNotificationLinkChanged(AuthToken token, Long caseId, DiffCollectionResult<CaseLink> linksDiff ) {
        publisherService.publishEvent( new CaseLinksEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, linksDiff ));
        return ok();
    }

    private boolean crossLinkAlreadyExist(List<CaseLink> caseLinks, Long remoteCaseId){
        return caseLinks.stream().anyMatch(cl -> Objects.equals(remoteCaseId, cl.getCaseId()));
    }

    private boolean isNotCrmLink(CaseLink link) {
        return !En_CaseLink.CRM.equals(link.getType());
    }

    private boolean checkLinksIsValid(Collection<CaseLink> caseLinks) {
        for (CaseLink link : caseLinks) {
            if ( link.getCaseId() == null || link.getType() == null || StringUtils.isBlank(link.getRemoteId())) {
                return false;
            }

            if (En_CaseLink.CRM.equals( link.getType()) && !NumberUtils.isDigits(link.getRemoteId())) {
                return false;
            }
        }
        return true;
    }

    private CaseLink createCrossCRMLink(Long linkedCaseId, Long caseId) {
        CaseLink crossLink = new CaseLink();
        crossLink.setType(En_CaseLink.CRM);
        crossLink.setRemoteId(String.valueOf(caseId));
        crossLink.setCaseId(linkedCaseId);

        return crossLink;
    }


    private Long parseRemoteIdAsLongValue(String remoteId) {
        try {
            return Long.parseLong(remoteId);
        } catch (NumberFormatException e) {
            log.warn("Can't parse linked case number link={}", remoteId);
            return null;
        }
    }

    private boolean isShowOnlyPrivateLinks(AuthToken token) {
        Set<UserRole> roles = token.getRoles();
        return !policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW);
    }

    private List<String> selectYouTrackLinkRemoteIds( Collection<CaseLink> links ) {
        return stream(links).filter(  caseLink -> YT.equals( caseLink.getType() )).map( CaseLink::getRemoteId ).collect( Collectors.toList() );
    }
}
