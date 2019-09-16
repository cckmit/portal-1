package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

/**
 * Сервис управления проектами и регионами
 */
@RemoteServiceRelativePath( "springGwtServices/RegionController" )
public interface RegionController extends RemoteService {

    List<RegionInfo> getRegionList(ProjectQuery query) throws RequestFailedException;

    List<EntityOption> getRegionList() throws RequestFailedException;

    List<DistrictInfo> getDistrictList() throws RequestFailedException;

    Map<String, List<Project>> getProjectsByRegions(ProjectQuery query) throws RequestFailedException;

    Project getProject(Long id) throws RequestFailedException;

    Project saveProject(Project project) throws RequestFailedException;

    long createNewProject() throws RequestFailedException;

    List<Project> getProjectsList(ProjectQuery query) throws RequestFailedException;

    List<EntityOption> getProjectsEntityOptionList(ProjectQuery query) throws RequestFailedException;

    Boolean removeProject(Long projectId) throws RequestFailedException;

    Project getProjectBaseInfo(Long id) throws RequestFailedException;
}
