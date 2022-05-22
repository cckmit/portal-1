package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface RegionControllerAsync {

    void getProjects(ProjectQuery query, AsyncCallback<SearchResult<Project>> callback);

    void getProjectOptionList(ProjectQuery query, AsyncCallback<List<EntityOption>> async);

    void getProjectInfoList(ProjectQuery query, AsyncCallback<List<ProjectInfo>> async);

    void getProject(Long id, AsyncCallback<Project> callback);

    void getProjectInfo(Long id, AsyncCallback<ProjectInfo> callback);

    void saveProject(Project project, AsyncCallback<UiResult<Project>> callback);

    void getRegionList(AsyncCallback<List<EntityOption>> callback);

    void removeProject(Long projectId, AsyncCallback<Long> async);

    void getProjectLeader(Long projectId, AsyncCallback<PersonShortView> async);
}
