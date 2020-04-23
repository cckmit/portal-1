package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Map;

/**
 * Асинхронный сервис управления продуктами
 */
public interface RegionControllerAsync {
    void getRegionList(ProjectQuery query, AsyncCallback< List< RegionInfo > > async);

    void getDistrictList(AsyncCallback<List<DistrictInfo>> callback);

    void getProjectsByRegions(ProjectQuery query, AsyncCallback<Map<String,List<Project>>> callback);

    void getProjects(ProjectQuery query, AsyncCallback<SearchResult<Project>> callback);

    void getProjectOptionList(ProjectQuery query, AsyncCallback<List<EntityOption>> async);

    void getProjectInfoList(ProjectQuery query, AsyncCallback<List<ProjectInfo>> async);

    void getProject(Long id, AsyncCallback<Project> callback);

    void getProjectInfo(Long id, AsyncCallback<ProjectInfo> callback);

    void saveProject(Project project, AsyncCallback<UiResult<Project>> callback);

    void getRegionList(AsyncCallback<List<EntityOption>> callback);

    void removeProject(Long projectId, AsyncCallback<Boolean> async);
}
