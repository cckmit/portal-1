package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.RFIDLabel;

import java.util.List;

public interface RFIDLabelControllerAsync {
    void get(Long id, AsyncCallback<RFIDLabel> async);

    void getAll(AsyncCallback<List<RFIDLabel>> async);

    void update(RFIDLabel value, AsyncCallback<RFIDLabel> async);
}
