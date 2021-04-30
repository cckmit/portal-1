package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.struct.ContractorQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/DeliveryController")
public interface DeliveryController extends RemoteService {

    SearchResult<Delivery> getDeliveries(DataQuery query) throws RequestFailedException;

    Delivery saveDelivery(Delivery delivery) throws RequestFailedException;
}
