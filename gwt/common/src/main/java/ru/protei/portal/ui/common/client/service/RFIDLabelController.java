package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;


@RemoteServiceRelativePath( "springGwtServices/RFIDLabelController" )
public interface RFIDLabelController extends RemoteService {
    RFIDLabel get(Long id) throws RequestFailedException;

    SearchResult<RFIDLabel> getByQuery(RFIDLabelQuery query) throws RequestFailedException;

    RFIDLabel update(RFIDLabel value) throws RequestFailedException;

    RFIDLabel remove(RFIDLabel value) throws RequestFailedException;
}

