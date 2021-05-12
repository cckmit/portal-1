package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

@RemoteServiceRelativePath("springGwtServices/DeliveryController")
public interface DeliveryController extends RemoteService {

    SearchResult<Delivery> getDeliveries(BaseQuery query) throws RequestFailedException;

    Delivery getDelivery(long id) throws RequestFailedException;

    Delivery saveDelivery(Delivery delivery) throws RequestFailedException;

    String getLastSerialNumber(boolean isArmyProject) throws RequestFailedException;
}
