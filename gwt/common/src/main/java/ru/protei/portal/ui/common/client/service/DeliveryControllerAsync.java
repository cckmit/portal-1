package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.struct.delivery.DeliveryNameAndDescriptionChangeRequest;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface DeliveryControllerAsync {

    void getDeliveries(BaseQuery query, AsyncCallback<SearchResult<Delivery>> async);

    void getDelivery(long id, AsyncCallback<Delivery> callback);

    void saveDelivery(Delivery delivery, AsyncCallback<Delivery> async);

    void getLastSerialNumber(boolean isArmyProject, AsyncCallback<String> async);

    void saveNameAndDescription(DeliveryNameAndDescriptionChangeRequest changeRequest, AsyncCallback<Void> callback);
}
