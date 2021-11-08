package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface PcbOrderControllerAsync {

    void getPcbOrderList(PcbOrderQuery query, AsyncCallback<SearchResult<PcbOrder>> async);

    void removePcbOrder(PcbOrder pcbOrder, AsyncCallback<PcbOrder> async);

    void savePcbOrder(PcbOrder pcbOrder, AsyncCallback<PcbOrder> async);
}
