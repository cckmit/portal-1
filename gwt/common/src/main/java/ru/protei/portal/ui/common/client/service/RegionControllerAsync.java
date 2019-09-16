package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

/**
 * Асинхронный сервис управления продуктами
 */
public interface RegionControllerAsync {
    void getRegionList(ProjectQuery query, AsyncCallback< List< RegionInfo > > async);

    void getDistrictList(AsyncCallback<List<DistrictInfo>> callback);

    void getProjectsByRegions(ProjectQuery query, AsyncCallback<Map<String,List<Project>>> callback);

    void getProjectsList(ProjectQuery query, AsyncCallback<List<Project>> callback);

    void getProject(Long id, AsyncCallback<Project> callback);

    void getProjectBaseInfo(Long id, AsyncCallback<Project> callback);

    void saveProject(Project project, AsyncCallback<Project> callback);

    void createNewProject(AsyncCallback<Long> callback);

    void getRegionList(AsyncCallback<List<EntityOption>> callback);

    void removeProject(Long projectId, AsyncCallback<Boolean> async);

    void getProjectsEntityOptionList(ProjectQuery query, AsyncCallback<List<EntityOption>> async);
}
