package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.RFIDLabel;

public interface RFIDLabelControllerAsync {
    void get(Long id, AsyncCallback<RFIDLabel> async);

    void update(RFIDLabel value, AsyncCallback<RFIDLabel> async);

    void remove(RFIDLabel value, AsyncCallback<RFIDLabel> async);

    void getLastScanLabel(boolean start, AsyncCallback<RFIDLabel> async);
}
