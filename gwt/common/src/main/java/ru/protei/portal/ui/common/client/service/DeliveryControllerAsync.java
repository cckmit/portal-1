package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface DeliveryControllerAsync {

    void getDeliveries(DeliveryQuery query, AsyncCallback<SearchResult<Delivery>> async);

    void getDelivery(long id, AsyncCallback<Delivery> callback);

    void saveDelivery(Delivery delivery, AsyncCallback<Delivery> async);

    void removeDelivery(Long id, AsyncCallback<Long> async);

    void getLastSerialNumber(boolean isMilitaryNumbering, AsyncCallback<String> async);

    void getLastSerialNumber(Long deliveryId, AsyncCallback<String> async);

    void updateNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest, AsyncCallback<Void> callback);

    void updateMeta(Delivery meta, AsyncCallback<Delivery> async);

    void updateMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers, AsyncCallback<CaseObjectMetaNotifiers> async);

    void addKits(List<Kit> kits, Long deliveryId, AsyncCallback<List<Kit>> async);

    void getKit(long kitId, AsyncCallback<Kit> callback);

    void updateKit(Kit kit, AsyncCallback<Kit> async);

    void getDeliveryStateId(long id, AsyncCallback<Long> callback);

    void updateKitListStates(List<Long> kitsIds, Long caseStateId, AsyncCallback<Void> async);
}
