package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/RFIDLabelController" )
public interface RFIDLabelController extends RemoteService {
    RFIDLabel get(Long id) throws RequestFailedException;

    List<RFIDLabel> getAll() throws RequestFailedException;

    RFIDLabel update(RFIDLabel value) throws RequestFailedException;
}

