package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

@RemoteServiceRelativePath("springGwtServices/CardBatchController")
public interface CardBatchController extends RemoteService {

    CardBatch saveCardBatch(CardBatch cardBatch) throws RequestFailedException;

    CardBatch getLastCardBatch(Long typeId) throws RequestFailedException;

    CardBatch updateMeta(CardBatch meta) throws RequestFailedException;

    SearchResult<CardBatch> getCardBatchesList(CardBatchQuery query) throws RequestFailedException;

    CardBatch getCardBatch(Long id) throws RequestFailedException;

    CardBatch updateCardBatch(CardBatch cardBatch) throws RequestFailedException;
}
