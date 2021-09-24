package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CardBatch;

public interface CardBatchControllerAsync {

    void saveCardBatch(CardBatch cardBatch, AsyncCallback<CardBatch> async);

    void getLastCardBatch(Long typeId, AsyncCallback<CardBatch> async);

    void updateMeta(CardBatch meta, AsyncCallback<CardBatch> async);
}
