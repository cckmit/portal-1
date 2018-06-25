package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.Map;

@RemoteServiceRelativePath( "springGwtServices/CaseLinkService" )
public interface CaseLinkService extends RemoteService {

    Map<En_CaseLink, String> getLinkMap() throws RequestFailedException;
}
