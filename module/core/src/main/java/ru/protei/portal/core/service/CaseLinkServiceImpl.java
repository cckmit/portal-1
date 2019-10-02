package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.winter.core.utils.collections.DiffCollectionResult;

import java.util.*;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.find;

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
    public Result<DiffCollectionResult<CaseLink>> mergeLinks( AuthToken token, Long caseId, Long caseNumber, List<CaseLink> caseLinks) {
        if (caseLinks == null) {
            return ok();
        }

        caseLinks.forEach(link -> link.setCaseId(caseId));
        if ( caseId == null || caseNumber == null || !checkLinksIsValid(caseLinks)) {
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
        DiffCollectionResult<CaseLink> caseLinksDiffResult = ru.protei.winter.core.utils.collections.CollectionUtils.diffCollection(oldCaseLinks, caseLinks);
        if ( CollectionUtils.isNotEmpty(caseLinksDiffResult.getRemovedEntries())) {
            Set<Long> toRemoveIds = new HashSet<>();
            caseLinksDiffResult.getRemovedEntries().forEach( link -> {
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

        if ( CollectionUtils.isNotEmpty(caseLinksDiffResult.getAddedEntries())) {
            List<CaseLink> toAddLinks = new ArrayList<>();
            CollectionUtils.emptyIfNull(caseLinksDiffResult.getAddedEntries()).forEach( link -> {
                if ( isNotCrmLink(link) ) {
                    return;
                }
                Long cId = parseRemoteIdAsLongValue(link.getRemoteId());
                if ( crossLinkAlreadyExist( oldCaseCrossLinks, cId )) {
                    return;
                }
                toAddLinks.add(createCrossCRMLink(cId, caseId));
            });
            toAddLinks.addAll(caseLinksDiffResult.getAddedEntries());
            caseLinkDAO.persistBatch(toAddLinks);
        }

        return ok(caseLinksDiffResult);
    }

    @Override
    public Result<YouTrackIssueInfo> getIssueInfo( AuthToken authToken, String ytId ) {
        return youtrackService.getIssueInfo( ytId );
    }

    @Override
    public Result<List<CaseLink>> getYoutrackLinks( Long caseId ) {
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setCaseId( caseId );
        caseLinkQuery.setType( En_CaseLink.YT );
        return ok(caseLinkDAO.getListByQuery(caseLinkQuery));
    }

    @Override
    @Transactional
    public Result<Long> addYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId ) {
        Long caseId = getCaseIdByCaseNumber( caseNumber );
        return getYoutrackLinks( caseId ).flatMap( caseLinks ->
                findCaseLinkByRemoterId( caseLinks, youtrackId ) ).map(
                CaseLink::getCaseId ).orElseGet( ignore ->
                addCaseLinkOnToYoutrack( caseId, youtrackId ) );
    }

    @Override
    public Result<Boolean> removeYoutrackLink( AuthToken authToken, Long caseNumber, String youtrackId ) {
        Long caseId = getCaseIdByCaseNumber( caseNumber );
        return getYoutrackLinks( caseId ).flatMap( caseLinks ->
                findCaseLinkByRemoterId( caseLinks, youtrackId ) ).flatMap( caseLink ->
                removeCaseLinkOnToYoutrack( caseLink )).orElseGet( ignore ->
                ok( true )
        );
    }

    private Long getCaseIdByCaseNumber( Long caseNumber ) {
        return caseObjectDAO.getCaseIdByNumber( caseNumber );
    }

    private Result<CaseLink> findCaseLinkByRemoterId( Collection<CaseLink> caseLinks, String youtrackId ) {
        return find( caseLinks, caseLink -> Objects.equals( caseLink.getRemoteId(), youtrackId ) )
                .map( Result::ok )
                .orElse( error( En_ResultStatus.NOT_FOUND ) );
    }

    private Result<Long> addCaseLinkOnToYoutrack( Long caseNumber, String youtrackId ) {
        CaseLink newLink = new CaseLink();
        newLink.setCaseId( caseNumber );
        newLink.setType( En_CaseLink.YT );
        newLink.setRemoteId( youtrackId );
        Long id = caseLinkDAO.persist( newLink );
        if (id == null) {
            log.error( "addCaseLinkOnToYoutrack(): Can`t add link on to youtrack into case, persistence error" );
            throw new RuntimeException( "addCaseLinkOnToYoutrack(): rollback transaction" );
        }
        return ok( id );
    }

    private Result<Boolean> removeCaseLinkOnToYoutrack( CaseLink caseLink ) {
        if (!caseLinkDAO.removeByKey( caseLink.getId() )) {
            log.error( "removeCaseLinkOnToYoutrack(): Can`t remove link on to youtrack, persistence error" );
            throw new RuntimeException( "removeCaseLinkOnToYoutrack(): rollback transaction" );
        }
        log.info( "removeCaseLinkOnToYoutrack(): removed CaseLink with id={}", caseLink.getId() );
        return ok(true);
    }

    private boolean crossLinkAlreadyExist(List<CaseLink> caseLinks, Long remoteCaseId){
        return caseLinks.stream().anyMatch(cl -> Objects.equals(remoteCaseId, cl.getCaseId()));
    }

    private boolean isNotCrmLink(CaseLink link) {
        return !En_CaseLink.CRM.equals(link.getType());
    }

    private boolean checkLinksIsValid(List<CaseLink> caseLinks) {
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
        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        return !policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW);
    }
}
