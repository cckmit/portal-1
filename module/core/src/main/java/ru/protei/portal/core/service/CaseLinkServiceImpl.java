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
import ru.protei.portal.core.event.ProjectLinkEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CaseStateUtil.isTerminalState;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;

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
    private UitsService uitsService;
    @Autowired
    private LockService lockService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private AuditObjectDAO auditObjectDAO;
    @Autowired
    private CaseShortViewDAO caseShortViewDAO;
    @Autowired
    private CaseService caseService;
    @Autowired
    CaseObjectMetaDAO caseObjectMetaDAO;
    @Autowired
    HistoryService historyService;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<Map<En_CaseLink, String>> getLinkMap() {
        Map<En_CaseLink, String> linkMap = new HashMap<>();
        linkMap.put(En_CaseLink.CRM, portalConfig.data().getCaseLinkConfig().getLinkCrm());
        linkMap.put(En_CaseLink.YT, portalConfig.data().getCaseLinkConfig().getLinkYouTrack());
        linkMap.put(En_CaseLink.UITS, portalConfig.data().getCaseLinkConfig().getLinkUits());
        return ok(linkMap);
    }

    @Override
    public Result<List<CaseLink>> getLinks(AuthToken token, Long caseId) {
        if (caseId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseId, isShowOnlyPublicLinks(token)));
        return ok(caseLinks);
    }

    @Override
    public Result<YouTrackIssueInfo> getYoutrackIssueInfo(AuthToken authToken, String ytId) {
        boolean isShowOnlyPublic = isShowOnlyPublicLinks(authToken);
        if (isShowOnlyPublic && En_CaseLink.YT.isForcePrivacy()) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        return youtrackService.getIssueInfo(ytId);
    }

    @Override
    public Result<UitsIssueInfo> getUitsIssueInfo(AuthToken authToken, Long uitsId) {
        boolean isShowOnlyPublic = isShowOnlyPublicLinks(authToken);
        if (isShowOnlyPublic && En_CaseLink.UITS.isForcePrivacy()) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        return uitsService.getIssueInfo(uitsId);
    }

    @Override
    @Transactional
    public Result<List<CaseLink>> createLinks(AuthToken authToken, List<CaseLink> linksToCreate, En_CaseType caseType) {

        if (isEmpty(linksToCreate)) {
            return ok();
        }

        List<CaseLink> successfullyCreatedLinks = new ArrayList<>();

        linksToCreate.forEach(caseLink -> {

            Result<CaseLink> createdLinkResult = createLink(caseLink, caseType, authToken);
            if (createdLinkResult.isOk()) {

                CaseLink createdLink = createdLinkResult.getData();

                Result<CaseObjectMeta> blockedParentIssue = blockParentIssue(authToken, createdLink);
                if (blockedParentIssue.isError()) {
                    throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED,
                            "failed to block parent issue with result  = " + blockedParentIssue + " | link = " + createdLink);
                }

                Result<CaseObjectMetaNotifiers> addedNotifierResult = addNotifierToSubtask(authToken, createdLink);
                if (addedNotifierResult.isError()) {
                    throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED,
                            "failed to add subtask notifier with result = " + addedNotifierResult + " | link = " + createdLink);
                }

                if (En_CaseType.CRM_SUPPORT.equals(caseType)){
                    addCaseLinkHistory(authToken, createdLink.getCaseId(), createdLink.getId(), createdLink.getRemoteId());
                }

                CaseLink newState = caseLinkDAO.get(createdLink.getId());
                successfullyCreatedLinks.add(newState);
            }
        });

        synchronizeYouTrackLinks(successfullyCreatedLinks, caseType);

        return ok(
                successfullyCreatedLinks,
                successfullyCreatedLinks.size() == linksToCreate.size() ? null : SOME_LINKS_NOT_SAVED
        );
    }

    @Override
    @Transactional
        public Result<CaseLink> createLinkWithPublish(AuthToken authToken, CaseLink link, En_CaseType caseType) {

        Result<CaseLink> createdLinkResult = createLink(link, caseType, authToken);
        if (createdLinkResult.isError()) {
            return error(createdLinkResult.getStatus());
        }

        CaseLink createdLink = createdLinkResult.getData();

        Result<CaseObjectMeta> blockedParentIssueResult = blockParentIssue(authToken, createdLink);
        if (blockedParentIssueResult.isError()) {
            log.error("createLinkWithPublish(): failed to block parent issue with result = {} | link = {}", blockedParentIssueResult, link);
            throw new RollbackTransactionException(blockedParentIssueResult.getStatus());
        }

        Result<CaseObjectMetaNotifiers> addedNotifierResult = addNotifierToSubtask(authToken, createdLink);
        if (addedNotifierResult.isError()) {
            log.error("createLinkWithPublish(): failed to add subtask notifier with result = {} | link = {}", addedNotifierResult, link);
            throw new RollbackTransactionException(addedNotifierResult.getStatus());
        }

        synchronizeYouTrackLinks(Collections.singletonList(link), caseType);

        if (En_CaseType.CRM_SUPPORT.equals(caseType)){
            addCaseLinkHistory(authToken, createdLink.getCaseId(), createdLink.getId(), createdLink.getRemoteId());
        }

        CaseLink newState = caseLinkDAO.get(createdLink.getId());
        return sendNotificationLinkAdded(authToken, link.getCaseId(), newState, caseType)
                .publishEvents(blockedParentIssueResult.getEvents());
    }

    @Override
    @Transactional
    public Result deleteLinks(AuthToken token, List<CaseLink> links) {
        if (isEmpty(links)) {
            return ok();
        }

        links.forEach(link -> {

            Result<CaseLink> deletedLinkResult = deleteLink(link.getId());
            if (deletedLinkResult.isOk()) {

                Result<CaseObjectMeta> openedIssueResult = openParentIssueIfAllLinksInTerminalState(token, deletedLinkResult.getData());
                if (openedIssueResult.isError()) {
                    log.error("deleteLinks(): linkId = {} | failed to open parent issue with result = {}", link.getId(), openedIssueResult);
                    throw new RollbackTransactionException(openedIssueResult.getStatus());
                }
            }
        });

        synchronizeYouTrackLinks(links);

        return ok();
    }

    @Override
    @Transactional
    public Result deleteLinkWithPublish (AuthToken authToken, Long id, En_CaseType caseType) {

        Result<CaseLink> deletedLinkResult = deleteLink(id);
        if (deletedLinkResult.isError()) {
            return error(deletedLinkResult.getStatus());
        }

        CaseLink deletedLink = deletedLinkResult.getData();

        Result<CaseObjectMeta> openedIssueResult = openParentIssueIfAllLinksInTerminalState(authToken, deletedLink);
        if (openedIssueResult.isError()) {
            log.error("deleteLinkWithPublish(): linkId = {} | failed to open parent issue with result = {}", id, openedIssueResult);
            throw new RollbackTransactionException(openedIssueResult.getStatus());
        }

        synchronizeYouTrackLinks(Collections.singletonList(deletedLink), caseType);

        if (En_CaseType.CRM_SUPPORT.equals(caseType)){
            removeCaseLinkHistory(authToken, deletedLink.getCaseId(), id, deletedLink.getRemoteId());
        }

        return sendNotificationLinkRemoved(authToken, deletedLink.getCaseId(), deletedLink, caseType)
                .publishEvents(openedIssueResult.getEvents());
    }

    private Result<Long> addCaseLinkHistory(AuthToken authToken, Long caseId, Long caseLinkId, String remoteId) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_LINK,null, null, caseLinkId, remoteId);
    }

    private Result<Long> removeCaseLinkHistory(AuthToken authToken, Long caseId, Long oldCaseLinkId, String oldRemoteId) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_LINK,oldCaseLinkId, oldRemoteId, null, null);
    }

    private Result<CaseObjectMeta> blockParentIssue(AuthToken authToken, CaseLink caseLink) {

        if (!En_CaseLink.CRM.equals(caseLink.getType())) return ok();

        boolean isParentFor = En_BundleType.PARENT_FOR.equals(caseLink.getBundleType());
        boolean isSubtask = En_BundleType.SUBTASK.equals(caseLink.getBundleType());

        if (isParentFor || isSubtask) {
            Long parentId = isParentFor ? caseLink.getCaseId() : NumberUtils.createLong(caseLink.getRemoteId());
            CaseObjectMeta caseObjectMeta = caseObjectMetaDAO.get(parentId);
            caseObjectMeta.setStateId(CrmConstants.State.BLOCKED);
            return caseService.updateCaseObjectMeta(authToken, caseObjectMeta);
        }

        return ok();
    }

    private Result<CaseObjectMeta> openParentIssueIfAllLinksInTerminalState(AuthToken authToken, CaseLink caseLink) {

        if (!En_CaseLink.CRM.equals(caseLink.getType())) return ok();

        boolean isParentFor = En_BundleType.PARENT_FOR.equals(caseLink.getBundleType());
        boolean isSubtask = En_BundleType.SUBTASK.equals(caseLink.getBundleType());

        if (isParentFor || isSubtask) {
            Long parentId = isParentFor ? caseLink.getCaseId() : NumberUtils.createLong(caseLink.getRemoteId());
            boolean isAllLinksInTerminalState = isAllLinksInTerminalState(parentId);
            if (isAllLinksInTerminalState) {
                CaseObjectMeta caseObjectMeta = caseObjectMetaDAO.get(parentId);
                caseObjectMeta.setStateId(CrmConstants.State.OPENED);
                return caseService.updateCaseObjectMeta(authToken, caseObjectMeta);
            }
        }

        return ok();
    }

    private boolean isAllLinksInTerminalState(Long caseObjectId) {
        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseObjectId, En_BundleType.PARENT_FOR));

        if (CollectionUtils.isNotEmpty(caseLinks) &&
                caseLinks.stream()
                        .allMatch(caseLink -> isTerminalState(caseLink.getCaseInfo().getState().getId()))) {
            return true;
        }
        return false;
    }

    private Result<CaseObjectMetaNotifiers> addNotifierToSubtask(AuthToken authToken, CaseLink caseLink) {

        if (!En_CaseLink.CRM.equals(caseLink.getType())) return ok();

        boolean isParentFor = En_BundleType.PARENT_FOR.equals(caseLink.getBundleType());
        boolean isSubtask = En_BundleType.SUBTASK.equals(caseLink.getBundleType());

        if (isParentFor || isSubtask) {
            Long subtaskId = isSubtask ? caseLink.getCaseId() : NumberUtils.createLong(caseLink.getRemoteId());
            Long parentId = isParentFor ? caseLink.getCaseId() : NumberUtils.createLong(caseLink.getRemoteId());
            CaseObject parent = caseObjectDAO.get(parentId);
            return caseService.addNotifierToCaseObject(authToken, subtaskId, parent.getManager());
        }

        return ok();
    }

    @Override
    @Transactional
    public Result<String> setYoutrackIdToCaseNumbers(AuthToken token, String youtrackId, List<Long> caseNumberList) {
        log.debug("setYoutrackIdToCaseNumbers(): youtrackId={}, case list size={}, caseList={}", youtrackId, caseNumberList.size(), caseNumberList);

        if (youtrackId == null) return error( En_ResultStatus.INCORRECT_PARAMS );

        Result<List<Long>> newCaseIdsResult = getCaseIdsByCaseNumbers(caseNumberList, En_CaseType.CRM_SUPPORT);
        if (newCaseIdsResult.isError()) {
            log.warn("setYoutrackIdToCaseNumbers(): fail to get newCaseIds, status={}", newCaseIdsResult.getStatus());
            return error(newCaseIdsResult.getStatus(), newCaseIdsResult.getMessage());
        }

        log.debug("setYoutrackIdToCaseNumbers(): newCaseIds={}", newCaseIdsResult.getData());

        List<Long> currentCaseIds =  getCaseIdsCrosslinkedWithYoutrack(youtrackId, En_CaseType.CRM_SUPPORT);
        List<Long> newCaseIds = newCaseIdsResult.getData();

        log.debug("setYoutrackIdToCaseNumbers(): current case ids={}, new case ids={}", currentCaseIds, newCaseIds);

        List<Long> listCaseIdsToAdd = makeListCaseIdsToAddYoutrackLink(currentCaseIds, newCaseIds);
        List<Long> listCaseIdsToRemove = makeListCaseIdsToRemoveYoutrackLink(currentCaseIds, newCaseIds);

        log.debug("setYoutrackIdToCaseNumbers(): listCaseIdsToAdd={}, listCaseIdsToRemove={}", listCaseIdsToAdd, listCaseIdsToRemove);

        Result<String> result = ok("");

        for (Long caseId : listCaseIdsToAdd) {
            Result<CaseLink> addResult = addYoutrackLinkWithPublishing(token, caseId, youtrackId, En_CaseType.CRM_SUPPORT);
            log.debug("setYoutrackIdToCaseNumbers(): adding caseId={}, addResult={}", caseId, addResult);

            if (addResult.isError()){
                return error(addResult.getStatus(), addResult.getMessage());
            }

            addResult.getEvents().forEach(result::publishEvent);
            makeAudit(caseId, youtrackId, En_AuditType.LINK_CREATE, token);
        }

        for (Long caseId : listCaseIdsToRemove) {
            makeAudit(caseId, youtrackId, En_AuditType.LINK_REMOVE, token);
            Result<CaseLink> removeResult = removeYoutrackLinkWithPublishing(token, caseId, youtrackId, En_CaseType.CRM_SUPPORT);
            log.debug("setYoutrackIdToCaseNumbers(): removing caseId={}, removeResult={}", caseId, removeResult);

            if (removeResult.isError()){
                return error(removeResult.getStatus(), removeResult.getMessage());
            }

            removeResult.getEvents().forEach(result::publishEvent);
        }

        return result;
    }

    @Override
    @Transactional
    public Result<String> setYoutrackIdToProjectNumbers(AuthToken token, String youtrackId, List<Long> projectNumberList) {
        log.debug("setYoutrackIdToProjectNumbers(): youtrackId={}, case list size={}, caseList={}", youtrackId, projectNumberList.size(), projectNumberList);

        if (youtrackId == null) return error( En_ResultStatus.INCORRECT_PARAMS );

        Result<String> unexistedProjects = getUnexistedProjects(projectNumberList);

        if (unexistedProjects.isError()){
            return error(unexistedProjects.getStatus(), unexistedProjects.getMessage());
        }

        List<Long> currentProjectIds = getCaseIdsCrosslinkedWithYoutrack(youtrackId, En_CaseType.PROJECT);
        List<Long> newProjectIds = projectNumberList;

        log.debug("setYoutrackIdToProjectNumbers(): current case ids={}, new case ids={}", currentProjectIds, newProjectIds);

        List<Long> listCaseIdsToAdd = makeListCaseIdsToAddYoutrackLink(currentProjectIds, newProjectIds);
        List<Long> listCaseIdsToRemove = makeListCaseIdsToRemoveYoutrackLink(currentProjectIds, newProjectIds);

        log.debug("setYoutrackIdToProjectNumbers(): listCaseIdsToAdd={}, listCaseIdsToRemove={}", listCaseIdsToAdd, listCaseIdsToRemove);

        Result<String> result = ok("");

        for (Long caseId : listCaseIdsToAdd) {
            Result<CaseLink> addResult = addYoutrackLinkWithPublishing(token, caseId, youtrackId, En_CaseType.PROJECT);
            log.debug("setYoutrackIdToProjectNumbers(): adding caseId={}, addResult={}", caseId, addResult);

            if (addResult.isError()){
                return error(addResult.getStatus(), addResult.getMessage());
            }

            addResult.getEvents().forEach(result::publishEvent);
            makeAudit(caseId, youtrackId, En_AuditType.LINK_CREATE, token);
        }

        for (Long caseId : listCaseIdsToRemove) {
            makeAudit(caseId, youtrackId, En_AuditType.LINK_REMOVE, token);
            Result<CaseLink> removeResult = removeYoutrackLinkWithPublishing(token, caseId, youtrackId, En_CaseType.PROJECT);
            log.debug("setYoutrackIdToProjectNumbers(): removing caseId={}, removeResult={}", caseId, removeResult);

            if (removeResult.isError()){
                return error(removeResult.getStatus(), removeResult.getMessage());
            }

            removeResult.getEvents().forEach(result::publishEvent);
        }

        return result;
    }

    @Override
    @Transactional
    public Result<String> changeYoutrackId(AuthToken token, String oldYoutrackId, String newYoutrackId) {
        log.debug("changeYoutrackId(): oldYoutrackId={}, newYoutrackId={}", oldYoutrackId, newYoutrackId);

        if (StringUtils.isEmpty(oldYoutrackId) || StringUtils.isEmpty(newYoutrackId)) {
            return error( En_ResultStatus.INCORRECT_PARAMS );
        }

        CaseLinkQuery query = new CaseLinkQuery();
        query.setType(En_CaseLink.YT);
        query.setRemoteId(oldYoutrackId);

        try {
            List<CaseLink> caseLinkList = caseLinkDAO.getListByQuery(query);

            log.debug("changeYoutrackId(): size caseLinkList={}", caseLinkList.size());

            caseLinkList.forEach(caseLink -> caseLink.setRemoteId(newYoutrackId));

            int batchSize = caseLinkDAO.mergeBatch(caseLinkList);

            if (batchSize != caseLinkList.size()) {
                log.warn("changeYoutrackId(): size caseLinkList={}, batchSize={}", caseLinkList.size(), batchSize);
            }
        } catch (Exception e){
            log.error("changeYoutrackId(): change failed", e);
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        return ok();
    }

    @Override
    public Result<List<Long>> getProjectIdsByYoutrackId(AuthToken token, String youtrackId) {
        log.debug("getProjectIdsByYoutrackId(): youtrackId={}", youtrackId);

        if (StringUtils.isEmpty(youtrackId)) {
            return error( En_ResultStatus.INCORRECT_PARAMS );
        }
        return ok(getCaseIdsCrosslinkedWithYoutrack(youtrackId, En_CaseType.PROJECT));
    }

    @Override
    public Result<CaseLink> getYtLink(AuthToken token, String youtrackId, Long caseId) {
        log.debug("getYtLink(): youtrackId={}, caseId={}", youtrackId, caseId);

        if (StringUtils.isEmpty(youtrackId) || caseId == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS );
        }
        return getYoutrackLinks(caseId)
                .flatMap(caseLinks -> findCaseLinkByRemoteId(caseLinks, youtrackId))
                .ifError(result -> log.warn("getYtLink(): failed to get link. youtrackId={}, caseId={}, result={}", youtrackId, caseId, result))
                .ifOk(caseLink -> log.debug("getYtLink(): OK. caseLink={}", caseLink));
    }

    private void synchronizeYouTrackLinks(List<CaseLink> links) {
        synchronizeYouTrackLinks(links, En_CaseType.CRM_SUPPORT);
        synchronizeYouTrackLinks(links, En_CaseType.PROJECT);
    }

    private void synchronizeYouTrackLinks(List<CaseLink> links, En_CaseType caseType) {
        links = getYouTrackLinks(links);

        if (isEmpty(links)) {
            return;
        }

        Set<String> remoteIds = toSet(links, CaseLink::getRemoteId);

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            for (String remoteId : remoteIds) {
                List<Long> caseNumbers = getCaseNumbersCrosslinkedWithYoutrack(remoteId, En_CaseType.CRM_SUPPORT);
                Result<YouTrackIssueInfo> result = youtrackService.setIssueCrmNumbers(remoteId, caseNumbers);

                if (result.isError()) {
                    log.warn("some links wasn't synchronized with youtrack. caseType={}, remoteId={}, caseNumbers={}", caseType, remoteId, caseNumbers);
                }
            }
        }

        if (En_CaseType.PROJECT.equals(caseType)) {
            for (String remoteId : remoteIds) {
                List<Long> caseIds = getCaseIdsCrosslinkedWithYoutrack(remoteId, En_CaseType.PROJECT);
                Result<YouTrackIssueInfo> result = youtrackService.setIssueProjectNumbers(remoteId, caseIds);

                if (result.isError()) {
                    log.warn("some links wasn't synchronized with youtrack. caseType={}, remoteId={}, caseIds={}", caseType, remoteId, caseIds);
                }
            }
        }
    }

    private Result<CaseLink> deleteLink(Long linkId) {
        Result<CaseLink> validationResult = validateLinkBeforeRemove(linkId);
        if (validationResult.isError()) {
            return error(validationResult.getStatus());
        }

        CaseLink linkToRemove = validationResult.getData();

        Set<Long> toRemoveIds = new HashSet<>();
        toRemoveIds.add(linkToRemove.getId());

        if (En_CaseLink.CRM.equals(linkToRemove.getType())){
            // удаляем зеркальные CRM-линки
            CaseLink crmCrosslink = caseLinkDAO.getCrmLink(En_CaseLink.CRM, NumberUtils.toLong(linkToRemove.getRemoteId()), linkToRemove.getCaseId().toString());
            if (crmCrosslink != null) {
                toRemoveIds.add(crmCrosslink.getId());
            }
        }

        caseLinkDAO.removeByKeys(toRemoveIds);

        return ok(linkToRemove);
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

    private En_ResultStatus validateLinkBeforeAdd(CaseLink link, AuthToken authToken){

        if (link == null || !isValidLink(link)) {
            return En_ResultStatus.INCORRECT_PARAMS;
        }

        if (Objects.equals(link.getRemoteId(), String.valueOf(link.getCaseId()))) {
            return En_ResultStatus.NOT_ALLOWED_LINK_ISSUE_TO_ITSELF;
        }

        CaseShortView caseObject = caseShortViewDAO.get(link.getCaseId());
        if (caseObject == null) {
            return En_ResultStatus.NOT_FOUND;
        }

        if (En_CaseLink.CRM.equals(link.getType())) {

            CaseShortView remoteObject = caseShortViewDAO.get(NumberUtils.createLong(link.getRemoteId()));
            if (remoteObject == null) {
                return En_ResultStatus.NOT_FOUND;
            }

            boolean isParentFor = En_BundleType.PARENT_FOR.equals(link.getBundleType());
            boolean isSubtask = En_BundleType.SUBTASK.equals(link.getBundleType());
            if (isParentFor || isSubtask) {
                CaseShortView parent = isParentFor ? caseObject : remoteObject;
                CaseShortView subtask = isParentFor ? remoteObject : caseObject;

                // запрещено создание ссылок для задач с автооткрытием
                if (parent.getAutoOpenIssue()) {
                    return En_ResultStatus.NOT_ALLOWED_AUTOOPEN_ISSUE;
                }
                if (subtask.getAutoOpenIssue()) {
                    return En_ResultStatus.NOT_ALLOWED_AUTOOPEN_ISSUE;
                }
                // запрещено создание ссылок, если родитель - интеграционная задача
                if (isIntegrationIssue(parent.getExtAppType())) {
                    return En_ResultStatus.NOT_ALLOWED_INTEGRATION_ISSUE;
                }
                // запрещено создание ссылок, если родитель в статусе "created" или "verified"
                if (isParentStateNotAllowed(parent.getStateId())) {
                    return En_ResultStatus.NOT_ALLOWED_PARENT_STATE;
                }
            }
        }

        // запрещено изменение ссылок вне зоны видимости
        boolean isShowOnlyPublic = isShowOnlyPublicLinks(authToken);
        if (isShowOnlyPublic && link.isPrivate()) {
            return En_ResultStatus.PERMISSION_DENIED;
        }

        boolean isAlreadyExist = caseLinkDAO.checkExistLink(link.getType(), link.getCaseId(), link.getRemoteId());
        if (isAlreadyExist) {
            return En_ResultStatus.THIS_LINK_ALREADY_ADDED;
        }

        return En_ResultStatus.OK;
    }

    private Result<CaseLink> createLink(CaseLink link, En_CaseType caseType, AuthToken token) {
        En_ResultStatus validationStatus = validateLinkBeforeAdd(link, token);
        if (!En_ResultStatus.OK.equals(validationStatus)) {
            return error(validationStatus);
        }

        Result<Long> addedLinkResult = lockService.doWithLockAndTransaction(CaseLink.class, link.getCaseId(), TimeUnit.SECONDS, 5, transactionTemplate, () -> {
            Long createdLinkId;

            switch (caseType) {
                case CRM_SUPPORT:

                    link.setWithCrosslink(true);
                    createdLinkId = caseLinkDAO.persist(link);
                    link.setId(createdLinkId);

                    if (En_CaseLink.CRM.equals(link.getType())) {
                        Long remoteId = NumberUtils.toLong(link.getRemoteId());

                        // для crm-линков создаем зеркальные
                        if (!caseLinkDAO.checkExistLink(En_CaseLink.CRM, remoteId, link.getCaseId().toString())) {
                            CaseLink crossCrmLink = new CaseLink();
                            crossCrmLink.setWithCrosslink(true);
                            crossCrmLink.setCaseId(remoteId);
                            crossCrmLink.setRemoteId(link.getCaseId().toString());
                            crossCrmLink.setType(En_CaseLink.CRM);
                            crossCrmLink.setBundleType(makeCrossBundleType(link.getBundleType()));
                            caseLinkDAO.persist(crossCrmLink);
                        }
                    }
                    return ok(createdLinkId);

                case PROJECT:

                    link.setWithCrosslink(En_CaseLink.YT.equals(link.getType()));
                    createdLinkId = caseLinkDAO.persist(link);
                    link.setId(createdLinkId);

                    return ok(createdLinkId);

                default:
                    link.setWithCrosslink(false);
                    createdLinkId = caseLinkDAO.persist(link);
                    link.setId(createdLinkId);

                    return ok(createdLinkId);
            }
        });

        if (addedLinkResult.isOk()) {
            return ok(link);
        }

        return error(addedLinkResult.getStatus(), "Link was not created");
    }

    private En_BundleType makeCrossBundleType(En_BundleType type) {
        switch (type) {
            case SUBTASK:
                return En_BundleType.PARENT_FOR;
            case PARENT_FOR:
                return En_BundleType.SUBTASK;
            default:
                return En_BundleType.LINKED_WITH;
        }
    }

    private Result<List<CaseLink>> getYoutrackLinks( Long caseId ) {
        if (caseId == null) return error( En_ResultStatus.INCORRECT_PARAMS );
        log.debug("getYoutrackLinks(): caseId={}", caseId);
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setCaseId( caseId );
        caseLinkQuery.setType( En_CaseLink.YT );
        log.debug("getYoutrackLinks(): caseLinkQuery={}", caseLinkQuery);
        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(caseLinkQuery);
        log.debug("getYoutrackLinks(): find caseLinks={}", caseLinks);
        return ok(caseLinks);
    }

    private Result<List<CaseLink>> getYoutrackLinks(  String youtrackId, Boolean withCrosslink) {
        log.debug("getYoutrackLinks(): youtrackId={}, withCrosslink={}", youtrackId, withCrosslink);
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setType( En_CaseLink.YT );
        caseLinkQuery.setRemoteId( youtrackId );
        caseLinkQuery.setWithCrosslink( withCrosslink );
        log.debug("getYoutrackLinks(): caseLinkQuery={}", caseLinkQuery);
        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(caseLinkQuery);
        log.debug("getYoutrackLinks(): find caseLinks={}", caseLinks);
        return ok(caseLinks);
    }

    private Result<CaseLink> findCaseLinkByRemoteId(Collection<CaseLink> caseLinks, String youtrackId ) {
        log.debug("findCaseLinkByRemoteId(): caseLinks={}, youtrackId={}", caseLinks, youtrackId);
        return find( caseLinks, caseLink -> youtrackId.equalsIgnoreCase(caseLink.getRemoteId()) )
                .map( Result::ok )
                .orElse( error( En_ResultStatus.NOT_FOUND ) );
    }

    private void makeAudit(Long caseId, String youtrackId, En_AuditType type, AuthToken token){
        List<CaseLink> linksList = getYoutrackLinks(caseId).getData();

        if (linksList == null || linksList.size() != 1){
            log.warn("makeAudit(): fail to find link with caseId={} and youtrackId={}", caseId, youtrackId);
            return;
        }

        AuditableObject object = linksList.get(0);
        AuditObject auditObject = new AuditObject();
        auditObject.setCreated( new Date() );
        auditObject.setType(type);
        auditObject.setCreatorId( token.getPersonId() );
        auditObject.setCreatorIp(token.getIp());
        auditObject.setCreatorShortName(token.getPersonDisplayShortName());
        auditObject.setEntryInfo(object);

        auditObjectDAO.insertAudit(auditObject);
    }

    private Result<CaseLink> addYoutrackLinkWithPublishing(AuthToken authToken, Long caseId, String youtrackId, En_CaseType caseType ) {

        return  addYoutrackCaseLink( caseId, youtrackId )
                .flatMap( addedLink -> sendNotificationLinkAdded( authToken, caseId, addedLink, caseType ));
    }

    private Result<CaseLink> removeYoutrackLinkWithPublishing(AuthToken authToken, Long caseId, String youtrackId, En_CaseType caseType ) {

        return getYoutrackLinks(caseId)
                .flatMap( caseLinks -> findCaseLinkByRemoteId( caseLinks, youtrackId ) )
                .flatMap(caseLink -> removeYoutrackCaseLink( caseLink )
                        .flatMap( removedLink -> sendNotificationLinkRemoved( authToken, caseId, removedLink, caseType))
        );
    }

    private List<Long> makeListCaseIdsToRemoveYoutrackLink(List<Long> currentCaseIds, List<Long> newCaseIds) {
        List<Long> removeList = new ArrayList<>(currentCaseIds);
        removeList.removeIf(currentCaseId -> newCaseIds.contains(currentCaseId));
        return removeList;
    }

    private List<Long> makeListCaseIdsToAddYoutrackLink(List<Long> currentCaseIds, List<Long> newCaseIds) {
        List<Long> addList = new ArrayList<>(newCaseIds);
        addList.removeIf(newCaseId -> currentCaseIds.contains(newCaseId));
        return addList;
    }

    private Result<List<Long>> getCaseIdsByCaseNumbers(List<Long> caseNumberList, En_CaseType caseType) {
        List<Long> caseIds = new ArrayList<>();
        List<Long> errorCaseId = new ArrayList<>();

        for (Long number : caseNumberList) {
            log.debug("getCaseIdsByCaseNumbers(): case number={}", number);
            Long caseId = caseObjectDAO.getCaseIdByNumber(caseType, number);
            log.debug("getCaseIdsByCaseNumbers(): case id={}", caseId);
            if (caseId == null) {
                errorCaseId.add(number);
            }
            caseIds.add(caseId);
        }

        return errorCaseId.isEmpty() ? ok(caseIds) :
                error(En_ResultStatus.NOT_FOUND,  errorCaseId.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")));
    }

    private List<Long> getCaseIdsCrosslinkedWithYoutrack( String youtrackId, En_CaseType caseType){
        List<CaseObject> cases = getCaseObjectsLinkedWithYoutrack(caseType, youtrackId, true);

        if (CollectionUtils.isEmpty(cases)){
            return new ArrayList<>();
        }

        return cases.stream()
                .map(caseObject -> caseObject.getId())
                .collect(Collectors.toList());
    }

    private List<Long> getCaseNumbersCrosslinkedWithYoutrack(String youtrackId, En_CaseType caseType){
        List<CaseObject> cases = getCaseObjectsLinkedWithYoutrack(caseType, youtrackId, true);

        if (CollectionUtils.isEmpty(cases)){
            return new ArrayList<>();
        }

        return cases.stream()
                .map(caseObject -> caseObject.getCaseNumber())
                .collect(Collectors.toList());
    }

    private List<CaseObject> getCaseObjectsLinkedWithYoutrack(En_CaseType caseType, String youtrackId, Boolean withCrosslink){
        List<Long> linkedCaseIds = getAllCaseIdsByYoutrackId(youtrackId, withCrosslink);

        if (CollectionUtils.isEmpty(linkedCaseIds)){
            return new ArrayList<>();
        }

        CaseQuery query = new CaseQuery();
        query.setCaseIds(linkedCaseIds);
        query.setType(caseType);
        return caseObjectDAO.getCases(query);
    }

    private Result<String> getUnexistedProjects(List<Long> projectNumberList){
        if (projectNumberList == null) {
            return ok();
        }

        List<Long> unexistedProjectList = new ArrayList<>();

        for (Long aLong : projectNumberList) {
            if (!caseObjectDAO.checkExistsByKey(aLong)){
                unexistedProjectList.add(aLong);
            }
        }

        return unexistedProjectList.isEmpty() ? ok() :
                error(En_ResultStatus.NOT_FOUND,  unexistedProjectList.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")));
    }

    private List<Long> getAllCaseIdsByYoutrackId(String youtrackId, Boolean withCrosslink) {
        return getYoutrackLinks(youtrackId, withCrosslink).getData().stream()
                .map(CaseLink::getCaseId)
                .collect(Collectors.toList());
    }

    private Result<CaseLink> addYoutrackCaseLink(Long caseId, String youtrackId) {
        log.debug("addYoutrackCaseLink(): caseId={}, youtrackId={}", caseId, youtrackId);
        CaseLink newLink = new CaseLink();
        newLink.setCaseId(caseId);
        newLink.setType(En_CaseLink.YT);
        newLink.setBundleType(En_BundleType.LINKED_WITH);
        newLink.setRemoteId(youtrackId);
        newLink.setWithCrosslink(true);
        Long id = caseLinkDAO.persist(newLink);
        if (id == null) {
            log.error("addYoutrackCaseLink(): Can`t add link on to youtrack into case, persistence error");
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED,
                    "addYoutrackCaseLink(): rollback transaction" );
        }
        newLink.setId(id);
        return ok(newLink);
    }

    private Result<CaseLink> removeYoutrackCaseLink(CaseLink caseLink ) {
        log.debug("removeCaseLinkOnToYoutrack(): caseLink={}", caseLink);
        if (!caseLinkDAO.removeByKey( caseLink.getId() )) {
            log.error( "removeCaseLinkOnToYoutrack(): Can`t remove link on to youtrack, persistence error" );
            throw new RollbackTransactionException(En_ResultStatus.NOT_REMOVED,
                    "removeCaseLinkOnToYoutrack(): rollback transaction" );
        }
        log.info( "removeCaseLinkOnToYoutrack(): removed CaseLink with id={}", caseLink.getId() );
        return ok(caseLink);
    }

    private Result<CaseLink> sendNotificationLinkAdded(AuthToken token, Long caseId, CaseLink added, En_CaseType caseType) {
        switch (caseType) {
            case PROJECT:
                return ok(added)
                        .publishEvent(new ProjectLinkEvent(this, added.getCaseId(), token.getPersonId(), added, null));
            case CRM_SUPPORT:
                return ok(added)
                        .publishEvent(new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, added, null));
            default:
                log.error("sendNotificationLinkRemoved(): Notification was not sent, caseType={}", caseType);
                return ok(added);
        }
    }

    private Result<CaseLink> sendNotificationLinkRemoved(AuthToken token, Long caseId, CaseLink removed, En_CaseType caseType) {
        switch (caseType) {
            case PROJECT:
                return ok(removed)
                        .publishEvent(new ProjectLinkEvent(this, removed.getCaseId(), token.getPersonId(), null, removed));
            case CRM_SUPPORT:
                return ok(removed)
                        .publishEvent( new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, null, removed ) );
            default:
                log.error( "sendNotificationLinkRemoved(): Notification was not sent, caseType={}", caseType );
                return ok(removed);
        }
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

    private List<CaseLink> getYouTrackLinks(Collection<CaseLink> links) {
        return stream(links)
                .filter(caseLink -> En_CaseLink.YT.equals(caseLink.getType()))
                .collect(toList());
    }

    private boolean isParentStateNotAllowed(Long stateId) {
        return isTerminalState(stateId) || CrmConstants.State.CREATED == stateId;
    }

    private boolean isIntegrationIssue(String extAppType) {
        En_ExtAppType type = En_ExtAppType.forCode(extAppType);
        if (type == null) {
            return false;
        }
        return true;
    }
}
