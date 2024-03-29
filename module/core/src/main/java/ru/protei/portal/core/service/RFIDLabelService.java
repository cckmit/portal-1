package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RFIDDevice;
import ru.protei.portal.core.model.ent.RFIDLabel;

public interface RFIDLabelService {
    Result<RFIDDevice> getOrCreateDeviceByReaderId(AuthToken token, String readerId);
    
    Result<RFIDLabel> saveOrUpdateLastScan(AuthToken token, RFIDLabel value);
    
    Result<RFIDLabel> getLastScanLabel(AuthToken token, boolean start);
}
