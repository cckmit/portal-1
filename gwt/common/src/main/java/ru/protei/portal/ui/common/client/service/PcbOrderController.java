package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

@RemoteServiceRelativePath("springGwtServices/PcbOrderController")
public interface PcbOrderController extends RemoteService {

    SearchResult<PcbOrder> getPcbOrderList(PcbOrderQuery query) throws RequestFailedException;

    PcbOrder getPcbOrder(Long pcbOrderId) throws RequestFailedException;

    PcbOrder savePcbOrder(PcbOrder pcbOrder) throws RequestFailedException;

    PcbOrder updateCommonInfo(PcbOrder pcbOrder) throws RequestFailedException;

    PcbOrder updateMeta(PcbOrder pcbOrder) throws RequestFailedException;

    PcbOrder updateMetaWithCreatingChildPbcOrder(PcbOrder pcbOrder, Integer receivedAmount) throws RequestFailedException;

    PcbOrder removePcbOrder(PcbOrder pcbOrder) throws RequestFailedException;
}
