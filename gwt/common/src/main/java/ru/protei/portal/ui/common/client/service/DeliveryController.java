package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/DeliveryController")
public interface DeliveryController extends RemoteService {

    SearchResult<Delivery> getDeliveries(DeliveryQuery query) throws RequestFailedException;

    Delivery getDelivery(long id) throws RequestFailedException;

    Delivery saveDelivery(Delivery delivery) throws RequestFailedException;

    long removeDelivery(Long id) throws RequestFailedException;

    String getLastSerialNumber(boolean isMilitaryNumbering) throws RequestFailedException;

    String getLastSerialNumber(Long deliveryId) throws RequestFailedException;

    void updateNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest) throws RequestFailedException;

    Delivery updateMeta(Delivery meta) throws RequestFailedException;

    CaseObjectMetaNotifiers updateMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers) throws RequestFailedException;

    List<Kit> createKits(List<Kit> kits, Long deliveryId) throws RequestFailedException;
}
