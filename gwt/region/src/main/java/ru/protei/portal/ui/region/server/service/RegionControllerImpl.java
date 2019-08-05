package ru.protei.portal.ui.region.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.DistrictQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.LocationService;
import ru.protei.portal.core.service.ProjectService;
import ru.protei.portal.ui.common.client.service.RegionController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса управления регионами
 */
@Service( "RegionController" )
public class RegionControllerImpl implements RegionController {

    @Override
    public List< RegionInfo > getRegionList( ProjectQuery query ) throws RequestFailedException {
        log.debug( "getRegionList(): search={} | showDeprecated={} | sortField={} | order={}",
                query.getSearchString(), query.getStates(), query.getSortField(), query.getSortDir() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< List< RegionInfo > > response = projectService.listRegions( descriptor.makeAuthToken(), query );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public List< DistrictInfo > getDistrictList() throws RequestFailedException {

        CoreResponse< List< DistrictInfo > > result = locationService.districtList( getDescriptorAndCheckSession().makeAuthToken(), new DistrictQuery() );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List< EntityOption > getRegionList() throws RequestFailedException {

        CoreResponse< List< EntityOption > > result = locationService.regionShortList( getDescriptorAndCheckSession().makeAuthToken() );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public Map< String, List< ProjectInfo > > getProjectsByRegions( ProjectQuery query ) throws RequestFailedException {
        log.debug( "getProjectsByRegions(): search={} | states={} | sortField={} | order={}",
                query.getSearchString(), query.getStates(), query.getSortField(), query.getSortDir() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Map< String, List< ProjectInfo > > > response = projectService.listProjectsByRegions( descriptor.makeAuthToken(), query );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public ProjectInfo getProject( Long id ) throws RequestFailedException {
        log.debug( "getProject(): id={}", id );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< ProjectInfo > response = projectService.getProject( descriptor.makeAuthToken(), id );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public void saveProject( ProjectInfo project ) throws RequestFailedException {
        log.debug( "saveProject(): project={}", project );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse response;
        if (project.getId() == null) {
            project.setCreated(new Date());
            project.setCreatorId(descriptor.getPerson().getId());
            response = projectService.createProject(descriptor.makeAuthToken(), project);
        }
        else {
            response = projectService.saveProject( descriptor.makeAuthToken(), project );
        }

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return;
    }

    @Override
    public long createNewProject() throws RequestFailedException {
        log.debug( "createNewProject()" );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Long > response = projectService.createProject( descriptor.makeAuthToken(), descriptor.getPerson().getId() );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public List<ProjectInfo> getProjectsList() throws RequestFailedException {
        log.debug( "getProjectsList()");

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< List< ProjectInfo > > response = projectService.listProjects( descriptor.makeAuthToken() );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public Boolean removeProject(Long projectId) throws RequestFailedException {
        log.debug("removeProject(): id={}", projectId);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Boolean> response = projectService.removeProject(descriptor.makeAuthToken(), projectId);
        log.debug("removeProject(): id={}, result={}", projectId, response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
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