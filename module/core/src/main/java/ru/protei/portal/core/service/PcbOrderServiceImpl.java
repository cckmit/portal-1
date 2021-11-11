package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.PcbOrderDAO;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Date;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.INCORRECT_PARAMS;

public class PcbOrderServiceImpl implements PcbOrderService {

    private static Logger log = LoggerFactory.getLogger(PcbOrderServiceImpl.class);

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    PcbOrderDAO pcbOrderDAO;
    @Autowired
    PolicyService policyService;

    @Override
    public Result<SearchResult<PcbOrder>> getPcbOrderList(AuthToken token, PcbOrderQuery query) {
        SearchResult<PcbOrder> sr = pcbOrderDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    public Result<PcbOrder> getPcbOrder(AuthToken token, Long pcbOrderId) {
        if (pcbOrderId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PcbOrder pcbOrder = pcbOrderDAO.get(pcbOrderId);
        if (pcbOrder == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        log.debug("getPcbOrder(): id = {}, result = {}", pcbOrderId, pcbOrder);
        return ok(pcbOrder);
    }

    @Override
    @Transactional
    public Result<PcbOrder> createPcbOrder(AuthToken token, PcbOrder pcbOrder) {
        if (pcbOrder == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!isValid(pcbOrder)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        fillDatesIfEmpty(pcbOrder);

        pcbOrder.setCreated(new Date());
        pcbOrder.setCreatorId(token.getPersonId());
        Long pcbOrderId = pcbOrderDAO.persist(pcbOrder);

        if (pcbOrderId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(pcbOrderDAO.get(pcbOrderId));
    }

    @Override
    @Transactional
    public Result<PcbOrder> updateCommonInfo(AuthToken token, PcbOrder commonInfo) {
        if (commonInfo == null || commonInfo.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isValid(commonInfo)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (!pcbOrderDAO.partialMerge(commonInfo, "card_type_id", "amount", "modification", "comment")) {
            log.warn("updateCommonInfo(): pcb order not updated. pbcOrder={}", commonInfo.getId());
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok(pcbOrderDAO.get(commonInfo.getId()));
    }

    @Override
    @Transactional
    public Result<PcbOrder> updateMeta(AuthToken token, PcbOrder meta) {
        if (meta == null || meta.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isValid(meta)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (!pcbOrderDAO.partialMerge(meta, "state", "promptness", "type", "stencil_type", "company_id",
                "order_date", "ready_date", "receipt_date")) {
            log.warn("updateMeta(): pcb order not updated. pbcOrder={}", meta.getId());
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok(pcbOrderDAO.get(meta.getId()));
    }

    @Override
    @Transactional
    public Result<PcbOrder> updateMetaWithCreatingChildPbcOrder(AuthToken token, PcbOrder parent, Integer receivedAmount) {
        if (parent == null || parent.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isValid(parent)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        PcbOrder childPcbOrder = createChildPcbOrder(parent, receivedAmount, token.getPersonId());
        Long childPcbOrderId = pcbOrderDAO.persist(childPcbOrder);
        if (childPcbOrderId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        parent.setAmount(parent.getAmount() - receivedAmount);
        if (!pcbOrderDAO.partialMerge(parent, "amount")) {
            log.warn("updateMetaWithCreatingChildPbcOrder(): pcb order not updated. pbcOrder={}", parent.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        return ok(pcbOrderDAO.get(parent.getId()));    }

    @Override
    @Transactional
    public Result<PcbOrder> removePcbOrder(AuthToken token, PcbOrder value) {
        if (value == null || value.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        PcbOrder pcbOrder = pcbOrderDAO.get(value.getId());
        if (pcbOrder == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        pcbOrderDAO.remove(pcbOrder);

        return ok(value);
    }

    private boolean isValid(PcbOrder pcbOrder) {
        if (pcbOrder.getCardTypeId() == null) {
            return false;
        }
        if (pcbOrder.getAmount() == null) {
            return false;
        }
        if (pcbOrder.getState() == null) {
            return false;
        }
        if (pcbOrder.getPromptness() == null) {
            return false;
        }
        if (pcbOrder.getType() == null) {
            return false;
        }
        if (pcbOrder.getCompanyId() == null) {
            return false;
        }
        return true;
    }

    private void fillDatesIfEmpty(PcbOrder pcbOrder) {
        if ((En_PcbOrderState.SENT.equals(pcbOrder.getState())
                || En_PcbOrderState.ACCEPTED.equals(pcbOrder.getState()))
                && pcbOrder.getOrderDate() == null) {
            pcbOrder.setOrderDate(new Date());
        }

        if (En_PcbOrderState.RECEIVED.equals(pcbOrder.getState())
                && pcbOrder.getReceiptDate() == null) {
            pcbOrder.setReceiptDate(new Date());
        }
    }

    private PcbOrder createChildPcbOrder(PcbOrder parent, Integer receivedAmount, Long recipientId) {
        PcbOrder child = new PcbOrder();
        child.setCardTypeId(parent.getCardTypeId());
        child.setAmount(receivedAmount);
        child.setModification(parent.getModification());
        child.setComment(parent.getComment());
        child.setState(En_PcbOrderState.RECEIVED);
        child.setPromptness(parent.getPromptness());
        child.setType(parent.getType());
        child.setStencilType(parent.getStencilType());
        child.setOrderDate(parent.getOrderDate());
        child.setReadyDate(parent.getReadyDate());
        child.setReceiptDate(new Date());
        child.setRecipientId(recipientId);
        child.setParentId(parent.getId());
        child.setCreated(new Date());
        child.setCreatorId(recipientId);
        child.setCompanyId(parent.getCompanyId());
        return child;
    }
}
