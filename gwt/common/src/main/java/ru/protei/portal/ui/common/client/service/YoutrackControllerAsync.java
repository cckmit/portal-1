package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.YoutrackProject;

import java.util.List;

public interface YoutrackControllerAsync {
    void getProjects(int offset, int limit, AsyncCallback<List<YoutrackProject>> async);
}
