package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/CardBatchController")
public interface CardBatchController extends RemoteService {

    CardBatch saveCardBatch(CardBatch cardBatch) throws RequestFailedException;

    CardBatch getLastCardBatch(Long typeId) throws RequestFailedException;

    CardBatch updateMeta(CardBatch meta) throws RequestFailedException;

    List<CardBatch> getListCardBatchByType(CardType cardType) throws RequestFailedException;
}
