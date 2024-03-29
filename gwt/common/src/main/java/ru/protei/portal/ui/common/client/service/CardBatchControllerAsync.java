package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface CardBatchControllerAsync {

    void saveCardBatch(CardBatch cardBatch, AsyncCallback<CardBatch> async);

    void getLastCardBatch(Long typeId, AsyncCallback<CardBatch> async);

    void getCardBatch(Long id, AsyncCallback<CardBatch> async);

    void updateCommonInfo(CardBatch commonInfo, AsyncCallback<CardBatch> async);

    void updateMeta(CardBatch meta, AsyncCallback<CardBatch> async);

    void getCardBatchesList(CardBatchQuery query, AsyncCallback<SearchResult<CardBatch>> async);

    void updateCardBatch(CardBatch meta, AsyncCallback<CardBatch> async);

    void getListCardBatchByType(CardType cardType, AsyncCallback<List<CardBatch>> async);

    void removeCardBatch(CardBatch value, AsyncCallback<CardBatch> async);
}
