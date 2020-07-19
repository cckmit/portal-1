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
import ru.protei.portal.core.model.dao.AuditObjectDAO;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.services.lock.LockService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private LockService lockService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private AuditObjectDAO auditObjectDAO;

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
    public Result<CaseLink> createLink(AuthToken authToken, CaseLink link, En_CaseType caseType) {

        En_ResultStatus validationStatus = validateLinkBeforeAdd(link, authToken);
        if (!En_ResultStatus.OK.equals(validationStatus)) {
            return error(validationStatus);
        }

        Long createdLinkId = addLink(link, caseType).getData();
        CaseLink createdLink = caseLinkDAO.get(createdLinkId);

        return ok(createdLink);
    }

    @Override
    @Transactional
    public Result<CaseLink> createLinkWithPublish(AuthToken authToken, CaseLink link, En_CaseType caseType) {

        En_ResultStatus validationStatus = validateLinkBeforeAdd(link, authToken);
        if (!En_ResultStatus.OK.equals(validationStatus)) {
            return error(validationStatus);
        }

        Long createdLinkId = addLink(link, caseType).getData();
        CaseLink createdLink = caseLinkDAO.get(createdLinkId);

        Result<CaseLink> completeResult = ok(createdLink);

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            completeResult.publishEvent(new CaseLinkEvent(this, ServiceModule.GENERAL, authToken.getPersonId(), link.getCaseId(), link, null));
        }

        if (En_CaseType.PROJECT.equals(caseType)) {
            completeResult.publishEvent(new ProjectLinkEvent(this, link.getCaseId(), authToken.getPersonId(), link, null));
        }

        return completeResult;
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
    public Result deleteLinkWithPublish (AuthToken authToken, Long id, En_CaseType caseType) {
        Result<CaseLink> validationResult = validateLinkBeforeRemove(id);
        if (validationResult.isError())
            return error(validationResult.getStatus());

        CaseLink existedLink = validationResult.getData();

        Result result = removeLink(existedLink);

        if (result.isError()) {
            return error(result.getStatus());
        }

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            result.publishEvent(new CaseLinkEvent(this, ServiceModule.GENERAL, authToken.getPersonId(), existedLink.getCaseId(), null, existedLink));
        }

        if (En_CaseType.PROJECT.equals(caseType)) {
            result.publishEvent(new ProjectLinkEvent(this, existedLink.getCaseId(), authToken.getPersonId(), null, existedLink));
        }

        return result;
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

        List<Long> currentCaseIds =  findLinkedCaseIdsByTypeAndYoutrackId(En_CaseType.CRM_SUPPORT, youtrackId);
        List<Long> newCaseIds = newCaseIdsResult.getData();

        log.debug("setYoutrackIdToCaseNumbers(): current case ids={}, new case ids={}", currentCaseIds, newCaseIds);

        List<Long> listCaseIdsToAdd = makeListCaseIdsToAddYoutrackLink(currentCaseIds, newCaseIds);
        List<Long> listCaseIdsToRemove = makeListCaseIdsToRemoveYoutrackLink(currentCaseIds, newCaseIds);

        log.debug("setYoutrackIdToCaseNumbers(): listCaseIdsToAdd={}, listCaseIdsToRemove={}", listCaseIdsToAdd, listCaseIdsToRemove);

        Result<String> result = ok("");

        for (Long caseId : listCaseIdsToAdd) {
            Result<Long> addResult = addYoutrackLink(token, caseId, youtrackId);
            log.debug("setYoutrackIdToCaseNumbers(): adding caseId={}, status={}", caseId, addResult.getStatus());

            if (addResult.isError()){
                return error(addResult.getStatus(), addResult.getMessage());
            }

            addResult.getEvents().forEach(event -> result.publishEvent(event));
            makeAudit(caseId, youtrackId, En_AuditType.LINK_CREATE, token);
        }

        for (Long caseId : listCaseIdsToRemove) {
            makeAudit(caseId, youtrackId, En_AuditType.LINK_REMOVE, token);
            Result<Long> removeResult = removeYoutrackLink(token, caseId, youtrackId);
            log.debug("setYoutrackIdToCaseNumbers(): removing caseId={}, status={}", caseId, removeResult.getStatus());

            if (removeResult.isError()){
                return error(removeResult.getStatus(), removeResult.getMessage());
            }

            removeResult.getEvents().forEach(event -> result.publishEvent(event));
        }

        return result;
    }

    @Override
    @Transactional
    public Result<String> setYoutrackIdToProjectNumbers(AuthToken token, String youtrackId, List<Long> projectNumberList) {
        log.debug("setYoutrackIdToProjectNumbers(): youtrackId={}, case list size={}, caseList={}", youtrackId, projectNumberList.size(), projectNumberList);

        if (youtrackId == null) return error( En_ResultStatus.INCORRECT_PARAMS );

        Result<String> unexistedProjects = findUnexistedProjects(projectNumberList);

        if (unexistedProjects.isError()){
            return error(unexistedProjects.getStatus(), unexistedProjects.getMessage());
        }

        List<Long> currentProjectIds = findLinkedCaseIdsByTypeAndYoutrackId(En_CaseType.PROJECT, youtrackId);
        List<Long> newProjectIds = projectNumberList;

        log.debug("setYoutrackIdToProjectNumbers(): current case ids={}, new case ids={}", currentProjectIds, newProjectIds);

        List<Long> listCaseIdsToAdd = makeListCaseIdsToAddYoutrackLink(currentProjectIds, newProjectIds);
        List<Long> listCaseIdsToRemove = makeListCaseIdsToRemoveYoutrackLink(currentProjectIds, newProjectIds);

        log.debug("setYoutrackIdToProjectNumbers(): listCaseIdsToAdd={}, listCaseIdsToRemove={}", listCaseIdsToAdd, listCaseIdsToRemove);

        Result<String> result = ok("");

        for (Long caseId : listCaseIdsToAdd) {
            Result<Long> addResult = addYoutrackLink(token, caseId, youtrackId);
            log.debug("setYoutrackIdToProjectNumbers(): adding caseId={}, status={}", caseId, addResult.getStatus());

            if (addResult.isError()){
                return error(addResult.getStatus(), addResult.getMessage());
            }

            addResult.getEvents().forEach(event -> result.publishEvent(event));
            makeAudit(caseId, youtrackId, En_AuditType.LINK_CREATE, token);
        }

        for (Long caseId : listCaseIdsToRemove) {
            makeAudit(caseId, youtrackId, En_AuditType.LINK_REMOVE, token);
            Result<Long> removeResult = removeYoutrackLink(token, caseId, youtrackId);
            log.debug("setYoutrackIdToProjectNumbers(): removing caseId={}, status={}", caseId, removeResult.getStatus());

            if (removeResult.isError()){
                return error(removeResult.getStatus(), removeResult.getMessage());
            }

            removeResult.getEvents().forEach(event -> result.publishEvent(event));
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
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return ok();
    }

    private Result removeLink (CaseLink link){
        Set<Long> toRemoveIds = new HashSet<>();
        toRemoveIds.add(link.getId());

        if (En_CaseLink.CRM.equals(link.getType())){
            // удаляем зеркальные CRM-линки
            CaseLink crmCrosslink = caseLinkDAO.getCrmLink(En_CaseLink.CRM, NumberUtils.toLong(link.getRemoteId()), link.getCaseId().toString());
            if (crmCrosslink != null) {
                toRemoveIds.add(crmCrosslink.getId());
            }
        }

        int removedCount = caseLinkDAO.removeByKeys(toRemoveIds);

        //Обновляем список ссылок на Youtrack
        if (En_CaseLink.YT.equals(link.getType())) {
            youtrackService.setIssueCrmNumbers(link.getRemoteId(), findLinkedCaseNumbersByTypeAndYoutrackId(En_CaseType.CRM_SUPPORT, link.getRemoteId()));
            youtrackService.setIssueProjectNumbers(link.getRemoteId(), findLinkedCaseIdsByTypeAndYoutrackId(En_CaseType.PROJECT, link.getRemoteId()));
        }

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

    private En_ResultStatus validateLinkBeforeAdd(CaseLink link, AuthToken authToken){

        if (link == null || !isValidLink(link)) {
            return En_ResultStatus.INCORRECT_PARAMS;
        }
        if (Objects.equals(link.getRemoteId(), String.valueOf(link.getCaseId()))) {
            return En_ResultStatus.NOT_ALLOWED_LINK_ISSUE_TO_ITSELF;
        }
        boolean isShowOnlyPublic = isShowOnlyPublicLinks(authToken);
        // запрещено изменение ссылок вне зоны видимости
        if (isShowOnlyPublic && link.isPrivate()) {
            return En_ResultStatus.PERMISSION_DENIED;
        }
        boolean isAlreadyExist = caseLinkDAO.checkExistLink(link.getType(), link.getCaseId(), link.getRemoteId());
        if (isAlreadyExist) {
            return En_ResultStatus.THIS_LINK_ALREADY_ADDED;
        }
        return En_ResultStatus.OK;
    }

    private Result<Long> addLink (CaseLink link, En_CaseType caseType) {
        return lockService.doWithLockAndTransaction(CaseLink.class, link.getCaseId(), TimeUnit.SECONDS, 5, transactionTemplate, () -> {

            Long createdLinkId = null;

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
                            caseLinkDAO.persist(crossCrmLink);
                        }
                    }
                    //Обновляем список ссылок на Youtrack
                    if (En_CaseLink.YT.equals(link.getType())) {
                        youtrackService.setIssueCrmNumbers(link.getRemoteId(), findLinkedCaseNumbersByTypeAndYoutrackId(En_CaseType.CRM_SUPPORT, link.getRemoteId()));
                    }
                    return ok(createdLinkId);

                case PROJECT:

                    if (En_CaseLink.CRM.equals(link.getType())) {
                        link.setWithCrosslink(false);
                        createdLinkId = caseLinkDAO.persist(link);
                        link.setId(createdLinkId);

                        return ok(createdLinkId);
                    }

                    if (En_CaseLink.YT.equals(link.getType())) {
                        Result<Boolean> isNeedCrosslink = youtrackService.checkExistProjectCustomField(link.getRemoteId());

                        if (isNeedCrosslink.isError()){
                            return error(En_ResultStatus.YOUTRACK_SYNCHRONIZATION_FAILED);
                        }

                        if (isNeedCrosslink.getData() == true){
                            link.setWithCrosslink(true);
                            createdLinkId = caseLinkDAO.persist(link);
                            link.setId(createdLinkId);

                            youtrackService.setIssueProjectNumbers(link.getRemoteId(), findLinkedCaseIdsByTypeAndYoutrackId(En_CaseType.PROJECT, link.getRemoteId()));

                            return ok(createdLinkId);
                        }
                        else {
                            link.setWithCrosslink(false);
                            createdLinkId = caseLinkDAO.persist(link);
                            link.setId(createdLinkId);

                            return ok(createdLinkId);
                        }
                    }

                default:

                    link.setWithCrosslink(false);
                    createdLinkId = caseLinkDAO.persist(link);
                    link.setId(createdLinkId);

                    return ok(createdLinkId);
            }
        });
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

    private Result<CaseLink> findCaseLinkByRemoteId(Collection<CaseLink> caseLinks, String youtrackId ) {
        log.debug("findCaseLinkByRemoteId(): caseLinks={}, youtrackId={}", caseLinks, youtrackId);
        return find( caseLinks, caseLink -> youtrackId.equalsIgnoreCase(caseLink.getRemoteId()) )
                .map( Result::ok )
                .orElse( error( En_ResultStatus.NOT_FOUND ) );
    }

    private void makeAudit(Long caseId, String youtrackId, En_AuditType type, AuthToken token){
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setCaseId( caseId );
        caseLinkQuery.setRemoteId(youtrackId);
        caseLinkQuery.setType( En_CaseLink.YT );
        List<CaseLink> linksList = caseLinkDAO.getListByQuery(caseLinkQuery);

        if (linksList.size() != 1){
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

    private Result<Long> addYoutrackLink( AuthToken authToken, Long caseId, String youtrackId ) {

        return  getYoutrackLinks(caseId)
                .flatMap( caseLinks -> findCaseLinkByRemoteId( caseLinks, youtrackId ) )
                .map( CaseLink::getCaseId )
                .orElseGet( ignore ->
                        addCaseLinkOnToYoutrack( caseId, youtrackId )
                                .flatMap( addedLink ->
                                        sendNotificationLinkAdded( authToken, caseId, addedLink )
                                )
                );
    }

    private Result<Long> removeYoutrackLink( AuthToken authToken, Long caseId, String youtrackId ) {

        return getYoutrackLinks(caseId)
                .flatMap( caseLinks -> findCaseLinkByRemoteId( caseLinks, youtrackId ) )
                .flatMap(caseLink -> removeCaseLinkOnToYoutrack( caseLink )
                        .flatMap( removedLink -> sendNotificationLinkRemoved( authToken, caseId, removedLink )
                )
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
            Long caseId = caseObjectDAO.getCaseId(caseType, number);
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

    private List<Long> findLinkedCaseIdsByTypeAndYoutrackId(En_CaseType caseType, String youtrackId){
        List<Long> linkedCaseIds = findAllCaseIdsByYoutrackId(youtrackId, true);

        if (linkedCaseIds != null && linkedCaseIds.isEmpty()){
            return new ArrayList<>();
        }

        CaseQuery query = new CaseQuery();
        query.setCaseIds(linkedCaseIds);
        query.setType(caseType);
        List<CaseObject> cases = caseObjectDAO.getCases(query);

        return cases.stream().map(caseObject -> caseObject.getId()).collect(Collectors.toList());
    }

    private List<Long> findLinkedCaseNumbersByTypeAndYoutrackId(En_CaseType caseType, String youtrackId){
        List<Long> linkedCaseIds = findAllCaseIdsByYoutrackId(youtrackId, true);

        if (linkedCaseIds != null && linkedCaseIds.isEmpty()){
            return new ArrayList<>();
        }

        CaseQuery query = new CaseQuery();
        query.setCaseIds(linkedCaseIds);
        query.setType(caseType);
        List<CaseObject> cases = caseObjectDAO.getCases(query);

        return cases.stream().map(caseObject -> caseObject.getCaseNumber()).collect(Collectors.toList());
    }

    private Result<String> findUnexistedProjects (List<Long> projectNumberList){
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

    private List<Long> findAllCaseIdsByYoutrackId(String youtrackId, Boolean withCrosslink) {
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setRemoteId( youtrackId );
        caseLinkQuery.setType( En_CaseLink.YT );
        caseLinkQuery.setWithCrosslink(withCrosslink);
        List<CaseLink> listByQuery = caseLinkDAO.getListByQuery(caseLinkQuery);

        return listByQuery.stream()
                .map(CaseLink::getCaseId)
                .collect(Collectors.toList());
    }

    /*//todo: переименовать в CrmNumbers, добавить тип при поиске CRM_SUPPORT,
    private List<Long> findAllCaseNumbersByYoutrackId(String youtrackId, Boolean withCrosslink){
        List<CaseObject> caseObjects = caseObjectDAO.getListByKeys(findAllCaseIdsByYoutrackId(youtrackId, withCrosslink));

        return caseObjects.stream()
                .map(CaseObject::getCaseNumber)
                .collect(Collectors.toList());
    }*/

    private Result<CaseLink> addCaseLinkOnToYoutrack( Long caseId, String youtrackId ) {
        log.debug("addCaseLinkOnToYoutrack(): caseId={}, youtrackId={}", caseId, youtrackId);
        CaseLink newLink = new CaseLink();
        newLink.setCaseId( caseId );
        newLink.setType( En_CaseLink.YT );
        newLink.setRemoteId( youtrackId );
        newLink.setWithCrosslink(true);
        Long id = caseLinkDAO.persist( newLink );
        if (id == null) {
            log.error( "addCaseLinkOnToYoutrack(): Can`t add link on to youtrack into case, persistence error" );
            throw new RollbackTransactionException( "addCaseLinkOnToYoutrack(): rollback transaction" );
        }
        newLink.setId( id );
        return ok( newLink );
    }

    private Result<CaseLink> removeCaseLinkOnToYoutrack( CaseLink caseLink ) {
        log.debug("removeCaseLinkOnToYoutrack(): caseLink={}", caseLink);
        if (!caseLinkDAO.removeByKey( caseLink.getId() )) {
            log.error( "removeCaseLinkOnToYoutrack(): Can`t remove link on to youtrack, persistence error" );
            throw new RollbackTransactionException( "removeCaseLinkOnToYoutrack(): rollback transaction" );
        }
        log.info( "removeCaseLinkOnToYoutrack(): removed CaseLink with id={}", caseLink.getId() );
        return ok(caseLink);
    }

    private Result<Long> sendNotificationLinkAdded(AuthToken token, Long caseId, CaseLink added ) {
        return ok(added.getId())
                .publishEvent( new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, added, null ));
    }

    private Result<Long> sendNotificationLinkRemoved(AuthToken token, Long caseId, CaseLink removed ) {
        return ok(removed.getId())
                .publishEvent( new CaseLinkEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, null, removed ) );
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
