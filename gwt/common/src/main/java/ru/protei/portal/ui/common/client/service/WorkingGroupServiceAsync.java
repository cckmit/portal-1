package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.WorkingGroup;

import java.util.List;

/**
 * Created by michael on 05.05.17.
 */
public interface WorkingGroupServiceAsync {
    void getAllGroups (AsyncCallback<List<WorkingGroup>> asyncCallback);
}
