package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.RFIDLabel;

public interface RFIDLabelControllerAsync {
    void getLastScanLabel(boolean start, AsyncCallback<RFIDLabel> async);
}
