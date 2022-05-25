package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.LocationService;
import ru.protei.portal.core.service.ProjectService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.RegionController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса управления регионами
 */
@Service( "RegionController" )
public class RegionControllerImpl implements RegionController {

    @Override
    public List< EntityOption > getRegionList() throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result< List< EntityOption > > result = locationService.regionShortList( token );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public Project getProject(Long id ) throws RequestFailedException {
        log.info( "getProject(): id={}", id );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Project> response = projectService.getProject( token, id );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public ProjectInfo getProjectInfo(Long id) throws RequestFailedException {
        log.info("getProjectInfo(): id={}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<ProjectInfo> response = projectService.getProjectInfo(token, id);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public UiResult<Project> saveProject(Project project) throws RequestFailedException {
        log.info("saveProject(): project={}", project);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Project> response;
        if (project.getId() != null) {
            response = projectService.saveProject(token, project);
        } else {
            project.setCreated(new Date());
            project.setCreatorId(token.getPersonId());
            response = projectService.createProject(token, project);
        }

        if ( response.isError() ) {
            log.info("saveProject(): status={}", response.getStatus());
            throw new RequestFailedException( response.getStatus() );
        }

        if (response.getMessage() != null) {
            log.info("saveProject(): message={}", response.getMessage());
        }

        return new UiResult<>(response.getData(), response.getMessage());
    }

    @Override
    public SearchResult<Project> getProjects(ProjectQuery query) throws RequestFailedException {
        log.info("getProjects(): query={}", query);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(projectService.projects(token, query));
    }

    @Override
    public List<EntityOption> getProjectOptionList(ProjectQuery query) throws RequestFailedException {
        log.info("getProjectsEntityOptionList(): query={}", query);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(projectService.listOptionProjects(token, query));
    }

    @Override
    public List<ProjectInfo> getProjectInfoList(ProjectQuery query) throws RequestFailedException {
        log.info("getProjectInfoList(): query={}", query);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(projectService.listInfoProjects(token, query));
    }

    @Override
    public Long removeProject(Long projectId) throws RequestFailedException {
        log.info("removeProject(): id={}", projectId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Long> response = projectService.removeProject(token, projectId);
        log.info("removeProject(): id={}, result={}", projectId, response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public PersonShortView getProjectLeader(Long projectId) throws RequestFailedException {
        log.info("getProjectLeader(): projectId={}", projectId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(projectService.getProjectLeader(token, projectId));
    }

    @Autowired
    LocationService locationService;

    @Autowired
    ProjectService projectService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(RegionControllerImpl.class);
}
