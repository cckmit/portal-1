package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления проектами и регионами
 */
@RemoteServiceRelativePath( "springGwtServices/RegionController" )
public interface RegionController extends RemoteService {

    List<EntityOption> getRegionList() throws RequestFailedException;

    Project getProject(Long id) throws RequestFailedException;

    UiResult<Project> saveProject(Project project) throws RequestFailedException;

    SearchResult<Project> getProjects(ProjectQuery query) throws RequestFailedException;

    List<EntityOption> getProjectOptionList(ProjectQuery query) throws RequestFailedException;

    List<ProjectInfo> getProjectInfoList(ProjectQuery query) throws RequestFailedException;

    Long removeProject(Long projectId) throws RequestFailedException;

    ProjectInfo getProjectInfo(Long id) throws RequestFailedException;

    PersonShortView getProjectLeader(Long projectId) throws RequestFailedException;
}
