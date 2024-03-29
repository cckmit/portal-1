package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;


@RemoteServiceRelativePath( "springGwtServices/RFIDLabelController" )
public interface RFIDLabelController extends RemoteService {
    RFIDLabel getLastScanLabel(boolean start) throws RequestFailedException;
}

