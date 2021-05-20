package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface DeliveryControllerAsync {

    void getDeliveries(DeliveryQuery query, AsyncCallback<SearchResult<Delivery>> async);

    void getDelivery(long id, AsyncCallback<Delivery> callback);

    void saveDelivery(Delivery delivery, AsyncCallback<Delivery> async);

    void getLastSerialNumber(boolean isArmyProject, AsyncCallback<String> async);

    void updateNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest, AsyncCallback<Void> callback);

    void updateMeta(Delivery meta, AsyncCallback<Delivery> async);

    void updateMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers, AsyncCallback<CaseObjectMetaNotifiers> async);
}
