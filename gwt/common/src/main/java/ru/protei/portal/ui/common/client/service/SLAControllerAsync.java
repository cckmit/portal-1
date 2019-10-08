package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;

import java.util.List;

public interface SLAControllerAsync {

    void getJiraSLAEntries(long mapId, AsyncCallback<List<JiraSLAMapEntry>> async);

    void getJiraSLAEntry(long mapId, String issueType, String severity, AsyncCallback<JiraSLAMapEntry> async);
}
