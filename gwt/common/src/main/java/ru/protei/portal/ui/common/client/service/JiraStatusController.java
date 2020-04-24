package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/JiraStatusController" )
public interface JiraStatusController extends RemoteService {
    List<JiraStatusMapEntry> getJiraStatusMapEntryList() throws RequestFailedException;
}
