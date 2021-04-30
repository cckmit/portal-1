package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface DeliveryControllerAsync {

    void getDeliveries(DataQuery query, AsyncCallback<SearchResult<Delivery>> async);

    void saveDelivery(Delivery delivery, AsyncCallback<Delivery> async);
}
