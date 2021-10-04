package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.CardBatchCreateEvent;
import ru.protei.portal.core.event.CardBatchUpdateEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.INCORRECT_PARAMS;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.CARD_BATCH_ARTICLE_PATTERN;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.CARD_BATCH_NUMBER_PATTERN;

/**
 * Реализация сервиса управления Партиями плат
 */
public class CardBatchServiceImpl implements CardBatchService {

    private static Logger log = LoggerFactory.getLogger(CardBatchServiceImpl.class);

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseNotifierDAO caseNotifierDAO;
    @Autowired
    CardBatchDAO cardBatchDAO;
    @Autowired
    CardDAO cardDAO;
    @Autowired
    HistoryService historyService;
    @Autowired
    CaseStateDAO caseStateDAO;
    @Autowired
    CaseMemberDAO caseMemberDAO;
    @Autowired
    PolicyService policyService;

    private final Pattern cardBatchNumber = Pattern.compile(CARD_BATCH_NUMBER_PATTERN);
    private final Pattern cardBatchArticle = Pattern.compile(CARD_BATCH_ARTICLE_PATTERN);

    @Override
    @Transactional
    public Result<CardBatch> createCardBatch(AuthToken token, CardBatch cardBatch) {

        if (cardBatch == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isValid(cardBatch, true)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Date now = new Date();
        CaseObject cardBatchCaseObject = createCardBatchCaseObject(null, cardBatch, token.getPersonId(), now, now);
        Long cardBatchCaseId = caseObjectDAO.persist(cardBatchCaseObject);
        if (cardBatchCaseId == null) {
            log.warn("createCardBatch(): case object not created, cardBatch={}", cardBatch);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }
        cardBatch.setId(cardBatchCaseId);

        try {
            updateContractors(cardBatchCaseObject, cardBatch.getContractors());
        } catch (Throwable e) {
            log.error("createCardBatch(): error during create card batch when set contractors;", e);
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        Long cardBatchId = cardBatchDAO.persist(cardBatch);
        if (cardBatchId == null) {
            log.warn("createCardBatch(): cardBatch not created, cardBatch={}", cardBatch);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        if(isNotEmpty(cardBatchCaseObject.getNotifiers())){
            caseNotifierDAO.persistBatch(
                    cardBatchCaseObject.getNotifiers()
                            .stream()
                            .map(person -> new CaseNotifier(cardBatchCaseId, person.getId()))
                            .collect(Collectors.toList()));

            jdbcManyRelationsHelper.fill(cardBatchCaseObject.getNotifiers(), Person.Fields.CONTACT_ITEMS);
        }

        long stateId = cardBatch.getStateId();
        Result<Long> resultState = addCardBatchStateHistory(token, cardBatchId, stateId, caseStateDAO.get(stateId).getState());
        if (resultState.isError()) {
            log.error("State message for the cardBatch {} not saved!", cardBatchCaseId);
        }

        //TODO доделать логику оповещения о создании партии плат
        CardBatchCreateEvent cardBatchCreateEvent = new CardBatchCreateEvent(this, token.getPersonId(), cardBatch.getId());

        return ok(cardBatchDAO.get(cardBatch.getId()), Collections.singletonList(cardBatchCreateEvent));
    }

    @Override
    @Transactional
    public Result<CardBatch> updateMeta(AuthToken token, CardBatch meta) {

        if (meta == null || meta.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isValid(meta, false)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CardBatch oldMeta = cardBatchDAO.get(meta.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        CaseObject caseObject = caseObjectDAO.get(meta.getId());
        caseObject = createCardBatchCaseObject(caseObject, meta, null, null, new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated) {
            log.info("Failed to update card batch meta data {} at db", caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        try {
            updateContractors(caseObject, meta.getContractors());
        } catch (Throwable e) {
            log.error("updateMeta(): error during save card batch meta when update contractors;", e);
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        isUpdated = cardBatchDAO.merge(meta);
        if (!isUpdated) {
            log.warn("updateMeta(): cardBatch not updated. cardBatch={}",  meta.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        //TODO доделать логику оповещения о редактировании партии плат
        CardBatchUpdateEvent updateEvent = new CardBatchUpdateEvent(this, oldMeta, meta, token.getPersonId());

        return ok(cardBatchDAO.get(caseObject.getId()), Collections.singletonList(updateEvent));
    }

    @Override
    @Transactional
    public Result<CardBatch> updateCardBatch(AuthToken token, CardBatch cardBatch) {

        Long cardBatchId = cardBatch != null ? cardBatch.getId() : null;

        if (cardBatchId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CardBatch oldCardBatch = cardBatchDAO.get(cardBatch.getId());
        if (oldCardBatch == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        boolean isUpdated = cardBatchDAO.merge(cardBatch);
        if (!isUpdated) {
            log.warn("updateCardBatch(): cardBatch not updated = {}",  cardBatch.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        return ok(cardBatch);
    }

        @Override
    public Result<CardBatch> getLastCardBatch(AuthToken token, Long typeId) {
        if (typeId == null) {
            return error(INCORRECT_PARAMS);
        }
        CardBatch cardBatch = cardBatchDAO.getLastCardBatch(typeId);
        log.debug("getLastCardBatch(): typeId = {}, result = {}", typeId, cardBatch);
        return ok(cardBatch);
    }

    @Override
    public Result<CardBatch> getCardBatch(AuthToken token, Long id) {
        if (id == null) {
            return error(INCORRECT_PARAMS);
        }
        CardBatch cardBatch = cardBatchDAO.get(id);

        if (cardBatch == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fillAll(cardBatch);

        log.debug("getCardBatch(): id = {}, result = {}", id, cardBatch);
        return ok(cardBatch);
    }

    @Override
    public Result<SearchResult<CardBatch>> getCardBatches(AuthToken token, CardBatchQuery query) {
        SearchResult<CardBatch> sr = cardBatchDAO.getSearchResultByQuery(query);

        if (CollectionUtils.isEmpty(sr.getResults())) {
            return ok();
        }

        Map<Long, Long> map = cardDAO.countByBatchIds(sr.getResults().stream()
                                     .map(CardBatch::getId)
                                     .collect(Collectors.toList()));

        jdbcManyRelationsHelper.fill(sr.getResults(), "members");

        sr.getResults().forEach(cardBatch -> {
            Long count = map.getOrDefault(cardBatch.getId(), 0L);
            cardBatch.setManufacturedAmount(count);
        });

        return ok(sr);
    }

    //обновление исполнителей партии плат
    private void updateContractors(CaseObject caseObject, List<PersonProjectMemberView> contractors) {

        List<PersonProjectMemberView> toAdd = listOf(contractors);
        List<Long> toRemove = new ArrayList<>();
        List<En_PersonRoleType> cardBatchRoles = En_PersonRoleType.getCardBatchRoles();

        if (caseObject.getMembers() != null) {
            for (CaseMember member : caseObject.getMembers()) {
                if (!cardBatchRoles.contains(member.getRole())) {
                    continue;
                }
                int nPos = toAdd.indexOf(new PersonProjectMemberView(member.getMember(), member.getRole()));
                if (nPos == -1) {
                    toRemove.add(member.getId());
                } else {
                    toAdd.remove(nPos);
                }
            }
        }

        if (toRemove.size() > 0) {
            caseMemberDAO.removeByKeys(toRemove);
        }

        if (toAdd.size() > 0) {
            caseMemberDAO.persistBatch(toAdd.stream()
                    .map(ppm -> {
                        CaseMember caseMember = new CaseMember();
                        caseMember.setMemberId(ppm.getId());
                        caseMember.setRole(ppm.getRole());
                        caseMember.setCaseId(caseObject.getId());
                        return caseMember;
                    })
                    .collect(Collectors.toList())
            );
        }
    }

    private Result<Long> addCardBatchStateHistory(AuthToken authToken, Long caseObjectId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, caseObjectId, En_HistoryAction.ADD, En_HistoryType.CARD_BATCH_STATE, null, null, stateId, stateName);
    }

    private boolean isValid(CardBatch cardBatch, boolean isNew) {

        if (isNew && cardBatch.getId() != null) {
            return false;
        }
        if (!isNumberValid(cardBatch.getNumber())) {
            return false;
        }
        if (!isArticleValid(cardBatch.getArticle())) {
            return false;
        }
        if (isNew && CrmConstants.State.BUILD_EQUIPMENT_IN_QUEUE != cardBatch.getStateId()) {
            return false;
        }
        if (cardBatch.getAmount() != null && cardBatch.getAmount() <= 0) {
            return false;
        }
        if (cardBatch.getTypeId() == null) {
            return false;
        }
        if (cardBatch.getDeadline() == null) {
            return false;
        }
        if (cardBatch.getImportance() == null) {
            return false;
        }
        return true;
    }

    private boolean isArticleValid(String article) {
        if (ru.protei.portal.core.model.helper.StringUtils.isEmpty(article)){
            return true;
        }
        return cardBatchArticle.matcher(article).matches();
    }

    private boolean isNumberValid(String number) {
        return ru.protei.portal.core.model.helper.StringUtils.isNotEmpty(number) &&
                cardBatchNumber.matcher(number).matches();
    }

    private CaseObject createCardBatchCaseObject(CaseObject caseObject, CardBatch cardBatch, Long creatorId,
                                                 Date created, Date modified) {
        if (caseObject == null){
            caseObject = new CaseObject();
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.CARD_BATCH));
            caseObject.setType(En_CaseType.CARD_BATCH);
            caseObject.setCreated(created);
            caseObject.setModified(created);
            caseObject.setCreatorId(creatorId);
        } else {
            caseObject.setModified(modified);
        }

        caseObject.setId(cardBatch.getId());
        caseObject.setName("");
        caseObject.setInfo(cardBatch.getParams());
        caseObject.setStateId(cardBatch.getStateId());
        caseObject.setImpLevel(cardBatch.getImportance());
        caseObject.setDeadline(cardBatch.getDeadline());

        return caseObject;
    }
}
