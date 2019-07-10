package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Реализация сервиса управления проектами
 */
public class ProjectServiceImpl implements ProjectService {

    private static Logger log = LoggerFactory.getLogger( ProjectServiceImpl.class );

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    LocationDAO locationDAO;

    @Autowired
    JdbcManyRelationsHelper helper;

    @Autowired
    CaseMemberDAO caseMemberDAO;

    @Autowired
    CaseLocationDAO caseLocationDAO;

    @Autowired
    CaseTypeDAO caseTypeDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    ProjectToProductDAO projectToProductDAO;

    @Autowired
    AuthService authService;

    @Override
    public CoreResponse< List< RegionInfo > > listRegions( AuthToken token, ProjectQuery query ) {

        LocationQuery locationQuery = new LocationQuery();
        locationQuery.setType( En_LocationType.REGION );
        List< Location > regions = locationDAO.listByQuery( locationQuery );
        Map< Long, RegionInfo > regionInfos = regions.stream().collect(
                Collectors.toMap( Location::getId, Location::toRegionInfo )
        );

        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType( En_CaseType.PROJECT );

        List<Long> productIds = null;
        if (query.getDirectionId() != null){
            productIds = new ArrayList<>();
            productIds.add( query.getDirectionId() );
        }
        caseQuery.setProductIds( productIds );

        List< CaseObject > projects = caseObjectDAO.listByQuery( caseQuery );
        projects.forEach( ( project ) -> {
            iterateAllLocations( project, ( location ) -> {
                applyCaseToRegionInfo( project, location, regionInfos );
            } );
        } );


        List< RegionInfo > result = regionInfos.values().stream()
                .filter( ( regionInfo ) -> {
                    if ( query.getStates() == null || query.getStates().isEmpty() ) {
                        return true;
                    }

                    return query.getStates().contains( regionInfo.state );
                } )
                .collect( toList() );

        return new CoreResponse< List< RegionInfo > >().success( result );
    }

    @Override
    public CoreResponse< Map< String, List< ProjectInfo > > > listProjectsByRegions( AuthToken token, ProjectQuery query ) {

        Map< String, List< ProjectInfo > > regionToProjectMap = new HashMap<>();
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType( En_CaseType.PROJECT );
        caseQuery.setStateIds( query.getStates().stream()
                .map( ( state ) -> new Long( state.getId() ).intValue() )
                .collect( toList() )
        );
        List<Long> productIds = null;
        if (query.getDirectionId() != null){
            productIds = new ArrayList<>();
            productIds.add( query.getDirectionId() );
        }
        caseQuery.setProductIds( productIds );
        if (query.isOnlyMineProjects()) {
            UserSessionDescriptor descriptor = authService.findSession(token);
            if (descriptor != null && descriptor.getPerson() != null) {
                caseQuery.setMemberIds(Collections.singletonList(descriptor.getPerson().getId()));
            }
        }

        List< CaseObject > projects = caseObjectDAO.listByQuery( caseQuery );
        projects.forEach( ( project ) -> {
            iterateAllLocations( project, ( location ) -> {
                applyCaseToProjectInfo( project, location, regionToProjectMap );
            } );
        } );

        return new CoreResponse< Map< String, List< ProjectInfo > > >().success( regionToProjectMap );
    }

    @Override
    public CoreResponse< ProjectInfo > getProject( AuthToken token, Long id ) {

        CaseObject caseObject = caseObjectDAO.get( id );
        helper.fillAll( caseObject );

        return new CoreResponse< ProjectInfo >().success( ProjectInfo.fromCaseObject( caseObject ) );
    }

    @Override
    @Transactional
    public CoreResponse saveProject( AuthToken token, ProjectInfo project ) {

        CaseObject caseObject = caseObjectDAO.get( project.getId() );
        helper.fillAll( caseObject );

        caseObject.setName( project.getName() );
        caseObject.setInfo( project.getDescription() );
        caseObject.setStateId( project.getState().getId() );
        if (project.getCustomerType() != null)
            caseObject.setLocal(project.getCustomerType().getId());

        if ( project.getProductDirection() == null ) {
            caseObject.setProductId( null );
        } else {
            caseObject.setProductId( project.getProductDirection().getId() );
        }

        if (project.getCustomer() == null) {
            caseObject.setInitiatorCompany(null);
        } else {
            caseObject.setInitiatorCompanyId(project.getCustomer().getId());
        }

        updateTeam( caseObject, project.getTeam() );
        updateLocations( caseObject, project.getRegion() );
        updateProducts( caseObject, project.getProducts() );

        caseObjectDAO.merge( caseObject );

        return new CoreResponse().success( null );
    }

    @Override
    @Transactional
    public CoreResponse<Long> createProject(AuthToken token, ProjectInfo project) {

        if (project == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = createCaseObjectFromProjectInfo(project);

        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_CREATED);

        updateProducts(caseObject, project.getProducts()); // ???

        return new CoreResponse().success(id);
    }

    private CaseObject createCaseObjectFromProjectInfo(ProjectInfo project) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.PROJECT));
        caseObject.setTypeId(En_CaseType.PROJECT.getId());
        caseObject.setCreated(new Date());
        caseObject.setStateId(En_RegionState.UNKNOWN.getId());
        caseObject.setCreatorId(project.getCreatorId());
        caseObject.setName(project.getName());
        caseObject.setInfo(project.getDescription());
        caseObject.setProducts(new HashSet<>());// ???
        if (project.getCustomer() == null) {
            caseObject.setInitiatorCompanyId(project.getCustomer().getId());
        }
        if (project.getCustomerType() != null) {
            caseObject.setLocal(project.getCustomerType().getId());
        }
        return caseObject;
    }

    @Override
    @Transactional
    public CoreResponse< Long > createProject( AuthToken token, Long creatorId ) {

        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber( caseTypeDAO.generateNextId(En_CaseType.PROJECT) );
        caseObject.setTypeId( En_CaseType.PROJECT.getId() );
        caseObject.setCreated( new Date() );
        caseObject.setName( "Новый проект" );
        caseObject.setInfo( "" );
        caseObject.setStateId( En_RegionState.UNKNOWN.getId() );
        caseObject.setCreatorId( creatorId );

        Long newId = caseObjectDAO.persist( caseObject );
        return new CoreResponse< Long >().success( newId );
    }

    @Override
    public CoreResponse<Boolean> removeProject(AuthToken token, Long projectId) {

        CaseObject caseObject = caseObjectDAO.get(projectId);

        if (caseObject == null) {
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_FOUND);
        }

        caseObject.setDeleted(true);
        boolean result = caseObjectDAO.partialMerge(caseObject, "deleted");

        return new CoreResponse<Boolean>().success(result);
    }

    @Override
    public CoreResponse<List<ProjectInfo>> listProjects(AuthToken authToken) {

        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.PROJECT);
        caseQuery.setSortDir(En_SortDir.ASC);
        caseQuery.setSortField(En_SortField.case_name);

        List<CaseObject> projects = caseObjectDAO.listByQuery(caseQuery);
        List<ProjectInfo> result = projects.stream()
                .map(ProjectInfo::fromCaseObject).collect(toList());
        return new CoreResponse<List<ProjectInfo>>().success( result );
    }

    private void updateTeam(CaseObject caseObject, List<PersonProjectMemberView> team) {

        List<PersonProjectMemberView> toAdd = new ArrayList<>(team);
        List<Long> toRemove = new ArrayList<>();
        List<En_DevUnitPersonRoleType> projectRoles = En_DevUnitPersonRoleType.getProjectRoles();

        for (CaseMember member : caseObject.getMembers()) {
            if (!projectRoles.contains(member.getRole())) {
                continue;
            }
            int nPos = toAdd.indexOf(PersonProjectMemberView.fromPerson(member.getMember(), member.getRole()));
            if (nPos == -1) {
                toRemove.add(member.getId());
            } else {
                toAdd.remove(nPos);
            }
        }

        if (toRemove.size() > 0) {
            caseMemberDAO.removeByKeys(toRemove);
        }

        if (toAdd.size() > 0) {
            caseMemberDAO.persistBatch(toAdd.stream()
                    .map(ppm -> {
                        CaseMember caseMember = new CaseMember();
                        caseMember.setMemberId(ppm.getId());
                        caseMember.setRole(ppm.getRole());
                        caseMember.setCaseId(caseObject.getId());
                        return caseMember;
                    })
                    .collect(Collectors.toList())
            );
        }
    }

    private void updateLocations( CaseObject caseObject, EntityOption location ) {
        boolean locationFound = false;

        if ( caseObject.getLocations() != null ) {
            for ( CaseLocation loc : caseObject.getLocations() ) {
                if ( loc.getLocationId().equals( location.getId() ) ) {
                    locationFound = true;
                    continue;
                }

                caseLocationDAO.removeByKey( loc.getId() );
            }
        }

        if ( locationFound ) {
            return;
        }

        if ( location == null ) {
            return;
        }

        caseLocationDAO.persist( CaseLocation.makeLocationOf( caseObject, location ) );

    }

    private void updateProducts(CaseObject caseObject, Set<ProductShortView> products) {
        if (products == null)
            return;

        Set<DevUnit> oldProducts = caseObject.getProducts();
        Set<DevUnit> newProducts = products.stream().map(DevUnit::fromProductShortView).collect(Collectors.toSet());

        Set<DevUnit> toDelete = new HashSet<>(oldProducts);
        Set<DevUnit> toCreate = new HashSet<>(newProducts);
        toCreate.removeAll(oldProducts);
        toDelete.removeAll(newProducts);

        ProjectToProduct projectToProduct = new ProjectToProduct(caseObject.getId(), null);

        toDelete.forEach(du -> {
            projectToProduct.setProductId(du.getId());
            projectToProductDAO.removeByKey(projectToProduct);
        });
        toCreate.forEach(du -> {
            projectToProduct.setProductId(du.getId());
            projectToProductDAO.persist(projectToProduct);
        });

        caseObject.setProducts(newProducts);
    }

    private void iterateAllLocations( CaseObject project, Consumer< Location > handler ) {
        if ( project == null ) {
            return;
        }

        helper.fillAll( project );

        List< CaseLocation > locations = project.getLocations();
        if ( locations == null || locations.isEmpty() ) {
            handler.accept( null );
            return;
        }

        locations.forEach( ( location ) -> {
            handler.accept( location.getLocation() );
        } );
    }

    private void applyCaseToRegionInfo( CaseObject project, Location location, Map< Long, RegionInfo > regions ) {
        RegionInfo region = findRegionByLocation( regions, location );
        if ( region == null ) {
            return;
        }

        if ( region.state == En_RegionState.UNKNOWN ) {
            region.state = En_RegionState.forId( project.getStateId() );
            return;
        }
    }

    private RegionInfo findRegionByLocation( Map< Long, RegionInfo > regions, Location location ) {
        if ( location == null ) {
            return null;
        }

        // добавить сюда поиск региона, если location у проекта не регион а муниципальное образование например
        if ( !En_LocationType.REGION.equals( location.getType() ) ) {
            return null;
        }

        RegionInfo info = regions.get( location.getId() );
        if ( info != null ) {
            return info;
        }

        return null;
    }

    private void applyCaseToProjectInfo( CaseObject project, Location location, Map< String, List< ProjectInfo > > projects ) {

        String locationName = ""; // name for empty location
        if ( location != null ) {
            locationName = location.getName();
        }

        List< ProjectInfo > projectInfos = projects.get( locationName );
        if ( projectInfos == null ) {
            projectInfos = new ArrayList<>();
            projects.put( locationName, projectInfos );
        }

        ProjectInfo projectInfo = ProjectInfo.fromCaseObject( project );
        projectInfos.add( projectInfo );
    }
}
