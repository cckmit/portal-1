package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface RFIDLabelControllerAsync {
    void get(Long id, AsyncCallback<RFIDLabel> async);

    void getByQuery(RFIDLabelQuery query, AsyncCallback<SearchResult<RFIDLabel>> async);

    void update(RFIDLabel value, AsyncCallback<RFIDLabel> async);

    void remove(RFIDLabel value, AsyncCallback<RFIDLabel> async);
}
