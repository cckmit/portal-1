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
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.services.lock.LockService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
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
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackService youtrackService;
    @Autowired
    private EventPublisherService publisherService;

    @Autowired
    private CaseService caseService;

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
    @Transactional
    public Result<List<CaseLink>> createLinks(AuthToken token, Long caseId, Long initiatorId, List<CaseLink> caseLinks) {
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

    @Override
    public Result<Long> createLink(AuthToken authToken, CaseLink link) {
        if (link == null || !isValidLink(link)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
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

        return lockService.doWithLockAndTransaction(CaseLink.class, link.getCaseId(), TimeUnit.SECONDS, 5, transactionTemplate, () -> {
            Long createdLinkId = caseLinkDAO.persist(link);
            link.setId(createdLinkId);

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

            publisherService.publishEvent( new CaseLinkEvent( this, ServiceModule.GENERAL, authToken.getPersonId(), link.getCaseId(), link, null ) );

            return ok(createdLinkId);
        });
    }

    @Override
    public Result removeLink(AuthToken authToken, Long id) {
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
                CaseLink mirrorCrmLink = caseLinkDAO.getCrmLink(En_CaseLink.CRM, NumberUtils.toLong(existedLink.getRemoteId()), existedLink.getCaseId().toString());
                if ( mirrorCrmLink != null ) {
                    toRemoveIds.add(mirrorCrmLink.getId());
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

        publisherService.publishEvent( new CaseLinkEvent( this, ServiceModule.GENERAL, authToken.getPersonId(), existedLink.getCaseId(), null, existedLink ) );

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

    private Result<Long> sendNotificationLinkAdded(AuthToken token, Long caseId, CaseLink added ) {
        publisherService.publishEvent( new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, added, null ));
        return ok(added.getId());
    }

    private Result<Long> sendNotificationLinkRemoved(AuthToken token, Long caseId, CaseLink removed ) {
        publisherService.publishEvent( new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, null, removed ));
        return ok(removed.getId());
    }

    private boolean isValidLink(CaseLink value) {
        if ( value.getCaseId() == null || value.getType() == null || StringUtils.isBlank(value.getRemoteId())) {
            return false;
        }

        return !En_CaseLink.CRM.equals(value.getType()) || NumberUtils.isDigits(value.getRemoteId());
    }

    private boolean isShowOnlyPublicLinks(AuthToken token) {
        return !policyService.hasGrantAccessFor(token.getRoles(), En_Privilege.ISSUE_VIEW);
    }

    private List<String> selectYouTrackLinkRemoteIds( Collection<CaseLink> links ) {
        return stream(links).filter(  caseLink -> YT.equals( caseLink.getType() )).map( CaseLink::getRemoteId ).collect( Collectors.toList() );
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
}
