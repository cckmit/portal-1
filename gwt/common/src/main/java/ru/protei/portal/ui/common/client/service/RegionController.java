package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
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

    Map<String, List<ProjectInfo>> getProjectsByRegions(ProjectQuery query) throws RequestFailedException;

    ProjectInfo getProject(Long id) throws RequestFailedException;

    ProjectInfo saveProject(ProjectInfo project) throws RequestFailedException;

    long createNewProject() throws RequestFailedException;

    List<ProjectInfo> getProjectsList(ProjectQuery query) throws RequestFailedException;

    Boolean removeProject(Long projectId) throws RequestFailedException;
}
