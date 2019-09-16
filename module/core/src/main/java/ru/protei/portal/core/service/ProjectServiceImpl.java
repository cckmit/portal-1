package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
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

    @Autowired
    ContractDAO contractDAO;

    @Autowired
    PersonDAO personDAO;

    @Override
    public Result< List< RegionInfo > > listRegions( AuthToken token, ProjectQuery query ) {

        List< Location > regions = locationDAO.listByQuery( makeLocationQuery(query, true ));
        /*  здесь на выходе получается мапа с сортировкой по id по возрастанию */
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
                    if (CollectionUtils.isEmpty(query.getStates())) {
                        return true;
                    }

                    return query.getStates().contains( regionInfo.state );
                } )
                .collect( toList() );

        return ok(result );
    }

    @Override
    public Result< Map< String, List<Project> > > listProjectsByRegions(AuthToken token, ProjectQuery query ) {

        Map< String, List<Project> > regionToProjectMap = new HashMap<>();
        CaseQuery caseQuery = applyProjectQueryToCaseQuery( token, query );
        caseQuery.setSortField(query.getSortField());
        caseQuery.setSortDir(query.getSortDir());

        List< CaseObject > projects = caseObjectDAO.listByQuery( caseQuery );
        projects.forEach( ( project ) -> {
            iterateAllLocations( project, ( location ) -> {
                applyCaseToProjectInfo( project, location, regionToProjectMap );
            } );
        } );

        return ok(regionToProjectMap );
    }

    @Override
    public Result<Project> getProject(AuthToken token, Long id ) {

        CaseObject project = caseObjectDAO.get( id );

        if (project == null) {
            return error(En_ResultStatus.NOT_FOUND, "Project was not found");
        }

        helper.fillAll( project );
        Contract contract = contractDAO.getByProjectId(id);

        if (contract != null) {
            project.setContractId(contract.getId());
            project.setContractNumber(contract.getNumber());
        }

        return ok(Project.fromCaseObject(project));
    }

    @Override
    public Result<Project> getProjectBaseInfo(AuthToken token, Long id) {
        CaseObject projectFromDb = caseObjectDAO.get(id);

        if (projectFromDb == null) {
            return error(En_ResultStatus.NOT_FOUND, "Project was not found");
        }

        Project project = new Project();
        project.setId(projectFromDb.getId());
        project.setName(projectFromDb.getName());
        project.setCustomerType(En_CustomerType.find(projectFromDb.getLocal()));
        project.setContragent(projectFromDb.getInitiatorCompany() == null ? null : new EntityOption(projectFromDb.getInitiatorCompany().getCname(), projectFromDb.getInitiatorCompanyId()));
        project.setProductDirection(projectFromDb.getProduct() == null ? null : new EntityOption(projectFromDb.getProduct().getName(), projectFromDb.getProductId()));
        project.setManager(projectFromDb.getManager() == null ? null : new EntityOption(projectFromDb.getManager().getDisplayShortName(), projectFromDb.getManagerId()));

        return ok(project);
    }

    @Override
    @Transactional
    public Result<Project> saveProject(AuthToken token, Project project ) {

        CaseObject caseObject = caseObjectDAO.get( project.getId() );
        helper.fillAll( caseObject );

        caseObject.setName( project.getName() );
        caseObject.setInfo( project.getDescription() );
        caseObject.setStateId( project.getState().getId() );
        caseObject.setManagerId(project.getTeam()
                .stream()
                .filter(personProjectMemberView -> personProjectMemberView.getRole().getId() == En_DevUnitPersonRoleType.HEAD_MANAGER.getId())
                .map(PersonShortView::getId)
                .findFirst()
                .orElse(null)
        );

        caseObject.setManager(personDAO.get(caseObject.getManagerId()));

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

        try {
            updateTeam( caseObject, project.getTeam() );
            updateLocations( caseObject, project.getRegion() );
            updateProducts( caseObject, project.getProducts() );
        } catch (Throwable e) {
            log.error("error during save project when update one of following parameters: team, location, or products; {}", e.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        caseObjectDAO.merge( caseObject );

        return ok(Project.fromCaseObject( caseObject ));
    }

    @Override
    @Transactional
    public Result<Project> createProject(AuthToken token, Project project) {

        if (project == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = createCaseObjectFromProjectInfo(project);

        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return error(En_ResultStatus.NOT_CREATED);

        try {
            updateTeam(caseObject, project.getTeam());
            updateLocations(caseObject, project.getRegion());
            updateProducts(caseObject, project.getProducts());
        } catch (Throwable e) {
            log.error("error during create project when set one of following parameters: team, location, or products; {}", e.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
        caseObjectDAO.merge( caseObject );

        return ok(Project.fromCaseObject(caseObject));
    }

    private CaseObject createCaseObjectFromProjectInfo(Project project) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.PROJECT));
        caseObject.setTypeId(En_CaseType.PROJECT.getId());
        caseObject.setCreated(project.getCreated() == null ? new Date() : project.getCreated());
        caseObject.setStateId(project.getState() == null ? En_RegionState.UNKNOWN.getId() : project.getState().getId());
        caseObject.setCreatorId(project.getCreatorId());
        caseObject.setName(project.getName());
        caseObject.setInfo(project.getDescription());
        caseObject.setManagerId(project.getTeam()
                .stream()
                .filter(personProjectMemberView -> personProjectMemberView.getRole() == En_DevUnitPersonRoleType.HEAD_MANAGER)
                .map(PersonShortView::getId)
                .findFirst()
                .orElse(null)
        );

        if (project.getProductDirection() != null)
            caseObject.setProductId(project.getProductDirection().getId());

        if (project.getCustomer().getId() != null) {
            caseObject.setInitiatorCompanyId(project.getCustomer().getId());
        }
        if (project.getCustomerType() != null) {
            caseObject.setLocal(project.getCustomerType().getId());
        }
        return caseObject;
    }

    @Override
    @Transactional
    public Result< Long > createProject( AuthToken token, Long creatorId ) {

        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber( caseTypeDAO.generateNextId(En_CaseType.PROJECT) );
        caseObject.setTypeId( En_CaseType.PROJECT.getId() );
        caseObject.setCreated( new Date() );
        caseObject.setName( "Новый проект" );
        caseObject.setInfo( "" );
        caseObject.setStateId( En_RegionState.UNKNOWN.getId() );
        caseObject.setCreatorId( creatorId );

        Long newId = caseObjectDAO.persist( caseObject );
        return ok(newId );
    }

    @Override
    public Result<Boolean> removeProject( AuthToken token, Long projectId) {

        CaseObject caseObject = caseObjectDAO.get(projectId);

        if (caseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        caseObject.setDeleted(true);
        boolean result = caseObjectDAO.partialMerge(caseObject, "deleted");

        return ok(result);
    }

    @Override
    public Result<List<Project>> listProjects(AuthToken authToken, ProjectQuery query) {
        CaseQuery caseQuery = applyProjectQueryToCaseQuery(authToken, query);

        List<CaseObject> projects = caseObjectDAO.listByQuery(caseQuery);

        helper.fill(projects, "members");
        helper.fill(projects, "products");

        List<Project> result = projects.stream()
                .map(Project::fromCaseObject).collect(toList());
        return ok(result );
    }

    private void updateTeam(CaseObject caseObject, List<PersonProjectMemberView> team) {

        List<PersonProjectMemberView> toAdd = new ArrayList<>(team);
        List<Long> toRemove = new ArrayList<>();
        List<En_DevUnitPersonRoleType> projectRoles = En_DevUnitPersonRoleType.getProjectRoles();

        if (caseObject.getMembers() != null) {
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

        Set<DevUnit> oldProducts = caseObject.getProducts() == null ? new HashSet<>() : caseObject.getProducts();
        Set<DevUnit> newProducts = products == null ? new HashSet<>() : products.stream().map(DevUnit::fromProductShortView).collect(Collectors.toSet());

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

    private void applyCaseToProjectInfo( CaseObject project, Location location, Map< String, List<Project> > projects ) {

        String locationName = ""; // name for empty location
        if ( location != null ) {
            locationName = location.getName();
        }

        List<Project> projectInfos = projects.get( locationName );
        if ( projectInfos == null ) {
            projectInfos = new ArrayList<>();
            projects.put( locationName, projectInfos );
        }

        Project projectInfo = Project.fromCaseObject( project );
        projectInfos.add( projectInfo );
    }

    private CaseQuery applyProjectQueryToCaseQuery(AuthToken authToken, ProjectQuery projectQuery) {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.PROJECT);

        if (CollectionUtils.isNotEmpty(projectQuery.getStates())) {
            caseQuery.setStateIds(projectQuery.getStates().stream()
                    .map((state) -> new Long(state.getId()).intValue())
                    .collect(toList())
            );
        }

        List<Long> productIds = null;
        if (projectQuery.getDirectionId() != null) {
            productIds = new ArrayList<>();
            productIds.add(projectQuery.getDirectionId());
        }
        if (CollectionUtils.isNotEmpty(projectQuery.getProductIds())) {
            productIds = new ArrayList<>();
            productIds.addAll(projectQuery.getProductIds());
        }
        caseQuery.setProductIds(productIds);

        if (projectQuery.isOnlyMineProjects() != null && projectQuery.isOnlyMineProjects()) {
            UserSessionDescriptor descriptor = authService.findSession(authToken);
            if (descriptor != null && descriptor.getPerson() != null) {
                caseQuery.setMemberIds(Collections.singletonList(descriptor.getPerson().getId()));
            }
        }

        if (projectQuery.getCustomerType() != null) {
            caseQuery.setLocal(projectQuery.getCustomerType().getId());
        }

        caseQuery.setCreatedFrom(projectQuery.getCreatedFrom());
        caseQuery.setCreatedTo(projectQuery.getCreatedTo());

        caseQuery.setSearchString(projectQuery.getSearchString());
        caseQuery.setSortDir(projectQuery.getSortDir());
        caseQuery.setSortField(projectQuery.getSortField());
        caseQuery.setFreeProject(projectQuery.getFreeProject());

        return caseQuery;
    }

    private LocationQuery makeLocationQuery( ProjectQuery query, boolean isSortByFilter ) {
        LocationQuery locationQuery = new LocationQuery();
        locationQuery.setType(En_LocationType.REGION);
        locationQuery.setSearchString(query.getSearchString());
        locationQuery.setDistrictIds(query.getDistrictIds());
        if (isSortByFilter) {
            locationQuery.setSortField(query.getSortField());
            locationQuery.setSortDir(query.getSortDir());
        }
        else {
            locationQuery.setSortField(En_SortField.name);
            locationQuery.setSortDir(En_SortDir.ASC);
        }
        return locationQuery;
    }
}
