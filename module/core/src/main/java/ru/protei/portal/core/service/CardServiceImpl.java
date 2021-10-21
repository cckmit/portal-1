package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class CardServiceImpl implements CardService {
    private static Logger log = LoggerFactory.getLogger(CardServiceImpl.class);
    public static final Long START_NUMBER = 0L;

    @Autowired
    CardDAO cardDAO;

    @Autowired
    CardTypeDAO cardTypeDAO;

    @Autowired
    CaseTypeDAO caseTypeDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    HistoryService historyService;

    @Autowired
    CaseStateDAO caseStateDAO;

    @Override
    public Result<Card> getCard(AuthToken token, Long id) {
        Card card = cardDAO.get(id);

        if (card == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(card);
    }

    @Override
    public Result<SearchResult<Card>> getCards(AuthToken token, CardQuery query) {
        SearchResult<Card> sr = cardDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    public Result<List<EntityOption>> getCardTypeOptionList(AuthToken token, CardTypeQuery query) {
        List<CardType> result = cardTypeDAO.listByQuery(query);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        List<EntityOption> options = result.stream()
                .filter(cardType -> query.getDisplay() == null || Objects.equals(cardType.isDisplay(), query.getDisplay()))
                .map(ct -> new EntityOption(ct.getName(), ct.getId()))
                .collect(Collectors.toList());

        return ok(options);
    }

    @Override
    public Result<List<CardType>> getCardTypeList(AuthToken token) {
        List<CardType> result = cardTypeDAO.getAll();

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(result);
    }

    @Override
    @Transactional
    public Result<Card> createCard(AuthToken token, Card card) {
        if (card == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!isValid(card)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CaseObject caseObject = createCaseObject(card, token.getPersonId());
        Long caseId = caseObjectDAO.persist(caseObject);
        if (caseId == null) {
            log.warn("create(): case object not created, card={}", card);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }
        card.setId(caseId);
        Long cardId = cardDAO.persist(card);
        if (cardId == null) {
            log.warn("create(): card not created, card={}", card);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        addCardStateHistory(token, card.getId(), card.getStateId(), caseStateDAO.get(card.getStateId()).getState())
                .ifError(ignore -> log.error("State message for the card {} not saved!", card));

        addManagerHistory(token, card.getId(), card.getManager().getId(), card.getManager().getDisplayShortName())
                .ifError(ignore -> log.error("Manager message for the card {} not saved!", card));

        return getCard(token, cardId);
    }

    @Override
    @Transactional
    public Result<Card> updateNoteAndComment(AuthToken token, Card card) {
        if (card == null || card.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValid(card)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        Card oldCard = cardDAO.get(card.getId());
        if (oldCard == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!isValid(oldCard, card)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CaseObject caseObject = caseObjectDAO.get(card.getId());
        boolean isUpdated;
        if (caseObject == null) {
            log.warn("Failed to find case object for card {} at db", card);
            throw new RollbackTransactionException(En_ResultStatus.NOT_FOUND);
        } else {
            caseObject.setInfo(card.getNote());
            caseObject.setModified(new Date());
            isUpdated = caseObjectDAO.partialMerge(caseObject, CaseObject.Columns.INFO, CaseObject.Columns.MODIFIED);
        }
        if (!isUpdated) {
            log.warn("Failed to update card note {} at db", card);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = cardDAO.partialMerge(card, "comment");
        if (!isUpdated) {
            log.warn("updateNoteAndComment(): card not updated. card={}",  card);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        return getCard(token, card.getId());
    }

    @Override
    @Transactional
    public Result<Card> updateMeta(AuthToken token, Card meta) {
        if (meta == null || meta.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValid(meta)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        Card oldMeta = cardDAO.get(meta.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!isValid(oldMeta, meta)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CaseObject caseObject = caseObjectDAO.get(meta.getId());
        boolean isUpdated;
        if (caseObject == null) {
            log.warn("Failed to find case object for card {} at db", meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_FOUND);
        } else {
            caseObject.setManagerId(meta.getManager().getId());
            caseObject.setStateId(meta.getStateId());
            caseObject.setModified(new Date());
            isUpdated = caseObjectDAO.partialMerge(caseObject, CaseObject.Columns.MANAGER,
                    CaseObject.Columns.STATE, CaseObject.Columns.MODIFIED);
        }
        if (!isUpdated) {
            log.info("Failed to update card meta data {} at db", meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = cardDAO.partialMerge(meta, "article", "test_date");
        if (!isUpdated) {
            log.warn("updateMeta(): card not updated. card={}",  meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        if (!Objects.equals(meta.getStateId(), oldMeta.getStateId())) {
            changeCardStateHistory(token, meta.getId(), oldMeta.getStateId(), caseStateDAO.get(oldMeta.getStateId()).getState(),
                    meta.getStateId(), caseStateDAO.get(meta.getStateId()).getState())
                        .ifError(ignore -> log.error("Change state message for the card {} not saved!", meta));
        }

        if (!Objects.equals(meta.getManager(), oldMeta.getManager())) {
            changeManagerHistory(token, meta.getId(),
                    oldMeta.getManager().getId(), oldMeta.getManager().getDisplayShortName(),
                    meta.getManager().getId(), meta.getManager().getDisplayShortName())
                        .ifError(ignore -> log.error("Change manager message for the card {} not saved!", meta));
        }

        return getCard(token, meta.getId());
    }

    @Override
    @Transactional
    public Result<Card> removeCard(AuthToken token, Card card) {
        if (card == null || card.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        CaseObject caseObject = caseObjectDAO.get(card.getId());
        if (caseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        caseObjectDAO.remove(caseObject);

        return ok(card);
    }

    public Result<Long> getLastNumber(AuthToken token, Long typeId, Long cardBatchId) {
        if (cardBatchId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long number = cardDAO.getLastNumber(typeId, cardBatchId);
        return ok(number != null? number : START_NUMBER);
    }

    private boolean isValid(Card card) {
        if (StringUtils.isEmpty(card.getSerialNumber())) {
            return false;
        }
        if (card.getStateId() == null) {
            return false;
        }
        if (card.getTypeId() == null) {
            return false;
        }
        if (card.getCardBatchId() == null) {
            return false;
        }
        if (card.getManager() == null) {
            return false;
        }
        if (card.getTestDate() == null) {
            return false;
        }
        return true;
    }

    private boolean isValid(Card oldCard, Card card) {
        return Objects.equals(oldCard.getTypeId(), card.getTypeId()) &&
                Objects.equals(oldCard.getCardBatchId(), card.getCardBatchId());
    }

    private CaseObject createCaseObject(Card card, Long creatorId) {
        Date now = new Date();
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.CARD));
        caseObject.setType(En_CaseType.CARD);
        caseObject.setCreated(now);
        caseObject.setModified(now);
        caseObject.setCreatorId(creatorId);
        caseObject.setName(Card.AUDIT_TYPE);
        caseObject.setId(card.getId());
        caseObject.setInfo(card.getNote());
        caseObject.setManagerId(card.getManager().getId());
        caseObject.setStateId(card.getStateId());

        return caseObject;
    }

    private Result<Long> addCardStateHistory(AuthToken authToken, Long caseObjectId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, caseObjectId, En_HistoryAction.ADD, En_HistoryType.CARD_STATE, null, null, stateId, stateName);
    }

    private Result<Long> changeCardStateHistory(AuthToken token, Long caseObjectId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(token, caseObjectId, En_HistoryAction.CHANGE, En_HistoryType.CARD_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Result<Long> addManagerHistory(AuthToken authToken, Long caseId, Long managerId, String ManagerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CARD_MANAGER, null, null, managerId, ManagerName);
    }

    private Result<Long> changeManagerHistory(AuthToken authToken, Long caseId, Long oldManagerId, String oldManagerName, Long newManagerId, String newManagerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CARD_MANAGER, oldManagerId, oldManagerName, newManagerId, newManagerName);
    }
}
