package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.INCORRECT_PARAMS;
import static ru.protei.portal.core.model.ent.CardBatch.Columns.TYPE_ID;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
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

        CaseObject cardBatchCaseObject = createCardBatchCaseObject(cardBatch, token.getPersonId());
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

        addCardBatchStateHistory(token, cardBatch.getId(), cardBatch.getStateId(), caseStateDAO.get(cardBatch.getStateId()).getState())
                .ifError(ignore -> log.error("State message for the cardBatch {} not saved!", cardBatch));

        addCardBatchImportanceHistory(token, cardBatch.getId(), cardBatch.getImportance().longValue(), cardBatch.getCode())
                .ifError(ignore -> log.error("Importance message for the cardBatch {} not saved!", cardBatch.getId()));

        return ok(cardBatchDAO.get(cardBatch.getId()));
    }

    @Override
    @Transactional
    public Result<CardBatch> updateCommonInfo(AuthToken token, CardBatch commonInfo) {

        if (commonInfo == null || commonInfo.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isCardBatchValid(commonInfo, false)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CardBatch oldCommonInfo = cardBatchDAO.get(commonInfo.getId());
        if (oldCommonInfo == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        CaseObject caseObject = caseObjectDAO.get(commonInfo.getId());
        boolean isUpdated;
        if (caseObject == null) {
            log.warn("Failed to find case object for card batch {} at db", commonInfo);
            throw new RollbackTransactionException(En_ResultStatus.NOT_FOUND);
        } else {
            caseObject.setInfo(commonInfo.getParams());
            caseObject.setModified(new Date());
            isUpdated = caseObjectDAO.partialMerge(caseObject, CaseObject.Columns.INFO, CaseObject.Columns.MODIFIED);
        }
        if (!isUpdated) {
            log.warn("Failed to update card batch common info {} at db", caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = cardBatchDAO.partialMerge(commonInfo, "id", "article", "amount");
        if (!isUpdated) {
            log.warn("updateMeta(): cardBatch not updated. cardBatch={}",  commonInfo.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        CardBatch cardBatch = cardBatchDAO.get(caseObject.getId());
        jdbcManyRelationsHelper.fillAll(cardBatch);
        return ok(cardBatch);
    }

    @Override
    @Transactional
    public Result<CardBatch> updateMeta(AuthToken token, CardBatch meta) {

        if (meta == null || meta.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isCardBatchValid(meta, false)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CardBatch oldMeta = cardBatchDAO.get(meta.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        CaseObject caseObject = caseObjectDAO.get(meta.getId());
        boolean isUpdated;
        if (caseObject == null) {
            log.warn("Failed to find case object for card batch {} at db", meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_FOUND);
        } else {
            caseObject.setStateId(meta.getStateId());
            caseObject.setImpLevel(meta.getImportance());
            caseObject.setDeadline(meta.getDeadline());
            caseObject.setModified(new Date());
            jdbcManyRelationsHelper.fill( caseObject, "members" );
            isUpdated = caseObjectDAO.partialMerge(caseObject, CaseObject.Columns.STATE, CaseObject.Columns.IMPORTANCE,
                    CaseObject.Columns.DEADLINE, CaseObject.Columns.MODIFIED);
        }
        if (!isUpdated) {
            log.warn("Failed to update card batch meta data {} at db", caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        try {
            updateContractors(caseObject, meta.getContractors());
        } catch (Throwable e) {
            log.error("updateMeta(): error during save card batch meta when update contractors;", e);
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        CardBatch cardBatch = cardBatchDAO.get(caseObject.getId());
        jdbcManyRelationsHelper.fillAll(cardBatch);

        if (!Objects.equals(oldMeta.getStateId(), cardBatch.getStateId())) {
            changeCardBatchStateHistory(token, cardBatch.getId(),
                    oldMeta.getStateId(), caseStateDAO.get(oldMeta.getStateId()).getState(),
                    cardBatch.getStateId(), caseStateDAO.get(cardBatch.getStateId()).getState())
                        .ifError(ignore -> log.error("Change state message for the cardBatch {} not saved!", cardBatch));
        }

        if (!Objects.equals(oldMeta.getImportance(), cardBatch.getImportance())) {
            changeCardBatchImportanceHistory(token, cardBatch.getId(),
                    oldMeta.getImportance().longValue(), oldMeta.getImportanceCode(),
                    cardBatch.getImportance().longValue(), cardBatch.getImportanceCode())
                        .ifError(ignore -> log.error("Importance level message for the cardBatch {} isn't saved!", cardBatch.getId()));
        }

        return ok(cardBatch);
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

        if (!Objects.equals(oldCardBatch.getStateId(), cardBatch.getStateId())) {
            changeCardBatchStateHistory(token, cardBatch.getId(),
                    oldCardBatch.getStateId(), caseStateDAO.get(oldCardBatch.getStateId()).getState(),
                    cardBatch.getStateId(), caseStateDAO.get(cardBatch.getStateId()).getState())
                        .ifError(ignore -> log.error("Change state message for the cardBatch {} not saved!", cardBatch));
        }

        if (!Objects.equals(oldCardBatch.getImportance(), cardBatch.getImportance())) {
            changeCardBatchImportanceHistory(token, cardBatch.getId(),
                    oldCardBatch.getImportance().longValue(), oldCardBatch.getImportanceCode(),
                    cardBatch.getImportance().longValue(), cardBatch.getImportanceCode())
                        .ifError(ignore -> log.error("Importance level message for the cardBatch {} isn't saved!", cardBatch.getId()));
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

        List<CardBatch> results = sr.getResults();
        if (isNotEmpty(results)) {
            jdbcManyRelationsHelper.fill(results, "members");
            Map<Long, Long> map = cardDAO.countByBatchIds(results.stream()
                    .map(CardBatch::getId)
                    .collect(Collectors.toList()));

            results.forEach(cardBatch -> {
                Long count = map.getOrDefault(cardBatch.getId(), 0L);
                cardBatch.setManufacturedAmount(count);
            });
        }

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

    @Override
    public Result<List<CardBatch>> getListCardBatchByType(AuthToken token, CardType cardType) {
        if (cardType == null) {
            return error(INCORRECT_PARAMS);
        }
        List<CardBatch> list = cardBatchDAO.getListByCondition(TYPE_ID + " = ?", cardType.getId());
        return ok(list);
    }

    @Override
    @Transactional
    public Result<CardBatch> removeCardBatch(AuthToken token, CardBatch value) {
        if (value == null || value.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        CaseObject caseObject = caseObjectDAO.get(value.getId());
        if (caseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (cardDAO.existByCardBatchId(value.getId())) {
            return error(En_ResultStatus.CARD_BATCH_HAS_CARD);
        }

        caseObjectDAO.remove(caseObject);

        return ok(value);
    }

    private Result<Long> addCardBatchStateHistory(AuthToken authToken, Long caseObjectId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, caseObjectId, En_HistoryAction.ADD, En_HistoryType.CARD_BATCH_STATE, null, null, stateId, stateName);
    }

    private Result<Long> changeCardBatchStateHistory(AuthToken token, Long caseObjectId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(token, caseObjectId, En_HistoryAction.CHANGE, En_HistoryType.CARD_BATCH_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Result<Long> addCardBatchImportanceHistory(AuthToken authToken, Long caseObjectId, Long importanceId, String importanceName) {
        return historyService.createHistory(authToken, caseObjectId, En_HistoryAction.ADD, En_HistoryType.CARD_BATCH_IMPORTANCE, null, null, importanceId, importanceName);
    }

    private Result<Long> changeCardBatchImportanceHistory(AuthToken authToken, Long caseId, Long oldImportanceId, String oldImportanceName, Long newImportanceId, String newImportanceName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CARD_BATCH_IMPORTANCE, oldImportanceId, oldImportanceName, newImportanceId, newImportanceName);
    }

    private boolean isValid(CardBatch cardBatch, boolean isNew) {

        if (isNew && cardBatch.getId() != null) {
            return false;
        }
        if (cardBatch.getTypeId() == null) {
            return false;
        }
        if (!isNumberValid(cardBatch.getNumber())) {
            return false;
        }
        if (!isArticleValid(cardBatch.getArticle())) {
            return false;
        }
        if (cardBatch.getAmount() != null && cardBatch.getAmount() <= 0) {
            return false;
        }

        return isCardBatchValid(cardBatch, isNew);
    }

    private boolean isCardBatchValid(CardBatch cardBatch, boolean isNew) {
        if (isNew && CrmConstants.State.BUILD_EQUIPMENT_IN_QUEUE != cardBatch.getStateId()) {
            return false;
        }
        if (cardBatch.getDeadline() == null) {
            return false;
        }
        if (cardBatch.getImportance() == null) {
            return false;
        }
        if (isEmpty(cardBatch.getContractors())) {
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

    private CaseObject createCardBatchCaseObject(CardBatch cardBatch, Long creatorId) {
        Date now = new Date();
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.CARD_BATCH));
        caseObject.setType(En_CaseType.CARD_BATCH);
        caseObject.setCreated(now);
        caseObject.setModified(now);
        caseObject.setCreatorId(creatorId);
        caseObject.setId(cardBatch.getId());
        caseObject.setName(CardBatch.AUDIT_TYPE);
        caseObject.setInfo(cardBatch.getParams());
        caseObject.setStateId(cardBatch.getStateId());
        caseObject.setImpLevel(cardBatch.getImportance());
        caseObject.setDeadline(cardBatch.getDeadline());

        return caseObject;
    }
}
