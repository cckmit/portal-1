package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

public interface JiraStatusControllerAsync {
    void getJiraStatusMapEntryList(AsyncCallback<List<JiraStatusMapEntry>> async);
}
