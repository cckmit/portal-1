package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.CardDAO;
import ru.protei.portal.core.model.dao.CardTypeDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class CardServiceImpl implements CardService {
    private static Logger log = LoggerFactory.getLogger(CardServiceImpl.class);
    @Autowired
    CardDAO cardDAO;

    @Autowired
    CardTypeDAO cardTypeDAO;

    @Autowired
    CaseTypeDAO caseTypeDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

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

        Date now = new Date();
        CaseObject caseObject = createCaseObject(null, card, token.getPersonId(), now, now);
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
        return getCard(token, cardId);
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

        CaseObject caseObject = caseObjectDAO.get(meta.getId());
        caseObject = createCaseObject(caseObject, meta, null, null, new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated) {
            log.info("Failed to update card meta data {} at db", meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = cardDAO.merge(meta);
        if (!isUpdated) {
            log.warn("updateMeta(): card not updated. card={}",  meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        return getCard(token, meta.getId());
    }

    private boolean isValid(Card card) {
        if (card.getTypeId() == null) {
            return false;
        }
        if (StringUtils.isEmpty(card.getSerialNumber())) {
            return false;
        }
        if (card.getStateId() == null) {
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

    private CaseObject createCaseObject(CaseObject caseObject, Card card,
                                        Long creatorId, Date created, Date modified) {
        if (caseObject == null){
            caseObject = new CaseObject();
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.CARD));
            caseObject.setType(En_CaseType.CARD);
            caseObject.setCreated(created);
            caseObject.setModified(created);
            caseObject.setCreatorId(creatorId);
        } else {
            caseObject.setModified(modified);
        }

        caseObject.setName(Card.AUDIT_TYPE);
        caseObject.setId(card.getId());
        caseObject.setInfo(card.getNote());
        caseObject.setManagerId(card.getManager().getId());
        caseObject.setStateId(card.getStateId());

        return caseObject;
    }
}
