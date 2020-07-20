package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;

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
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

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

    @Autowired
    PlatformDAO platformDAO;

    @Autowired
    CaseLinkService caseLinkService;

    @Autowired
    ProjectDAO projectDAO;
    @Autowired
    PortalScheduleTasks scheduledTasksService;
    @Autowired
    EventPublisherService publisherService;


    @EventListener
    @Async(BACKGROUND_TASKS)
    @Override
    public void schedulePauseTimeNotificationsOnPortalStartup( SchedulePauseTimeOnStartupEvent event ) {
        Collection<Project> projects = projectDAO.selectScheduledPauseTime( System.currentTimeMillis() );

        if (isEmpty( projects )) {
            log.info( "schedulePauseTimeNotificationsOnPortalStartup(): No planned pause time notifications found." );
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "YYYY.MM.dd HH:mm:ss" );
        for (Project project : projects) {
            log.info( "schedulePauseTimeNotificationsOnPortalStartup(): projectId={} date={}", project.getId(), simpleDateFormat.format( project.getPauseDate() ) );

            ProjectPauseTimeHasComeEvent projectPauseTimeEvent = new ProjectPauseTimeHasComeEvent( this, project.getId(), project.getPauseDate() );
            scheduledTasksService.scheduleEvent( projectPauseTimeEvent, new Date( project.getPauseDate() ) );
        }
    }

    @EventListener
    @Async(BACKGROUND_TASKS)
    @Override
    public void onPauseTimeNotification( ProjectPauseTimeHasComeEvent event ) {
        Long projectId = event.getProjectId();
        Long pauseDate = event.getPauseDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "YYYY.MM.dd HH:mm:ss" );
        log.info( "onPauseTimeNotification(): {} {}", projectId, simpleDateFormat.format( pauseDate ) );

        Project project = projectDAO.get( projectId );
        if (!Objects.equals( pauseDate, project.getPauseDate() )) {
            log.info( "onPauseTimeNotification(): Ignore notification: pause date was changed: old {} new {}", simpleDateFormat.format( pauseDate ), simpleDateFormat.format( project.getPauseDate() ) );
            return;
        }

        jdbcManyRelationsHelper.fill( project, "members" );

        Collection<Long> subscribersIds = toSet( project.getMembers(), caseMember -> caseMember.getMemberId() );
        if (project.getCreatorId() != null) {
            subscribersIds.add( project.getCreatorId() );
        }

        if (isEmpty( subscribersIds )) {
            log.info( "onPauseTimeNotification(): Ignore notification: No subscribers found for pause time notification {}", simpleDateFormat.format( pauseDate ) );
            return;
        }

        PersonQuery personQuery = new PersonQuery();
        personQuery.setDeleted( false );
        personQuery.setFired( false );
        personQuery.setPeople( true );
        personQuery.setPersonIds( subscribersIds );
        List<Person> persons = personDAO.getPersons( personQuery );

        if (isEmpty( persons )) {
            log.info( "onPauseTimeNotification(): Ignore notification: No available subscribers found for pause time notification {}", simpleDateFormat.format( pauseDate ) );
            return;
        }

        log.info( "onPauseTimeNotification(): Do notification: pause date {} subscribers: {}", simpleDateFormat.format( pauseDate ), persons);
        for (Person person : persons) {
            publisherService.publishEvent( new ProjectPauseTimeNotificationEvent( this, person, project.getId(), project.getName(), new Date(pauseDate) ) );
        }
    }

    @Override
    public Result< List< RegionInfo > > listRegions( AuthToken token, ProjectQuery query ) {
        List< Location > regions = locationDAO.listByQuery( makeLocationQuery(query, true ));
        return ok(regions.stream().map(Location::toRegionInfo).collect(toList()));
    }

    @Override
    public Result< Map< String, List<Project> > > listProjectsByRegions(AuthToken token, ProjectQuery query ) {

        Map< String, List<Project> > regionToProjectMap = new HashMap<>();
        CaseQuery caseQuery = query.toCaseQuery(token.getPersonId());
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

        Platform platform = platformDAO.getByProjectId(id);

        if (platform != null) {
            project.setPlatformId(platform.getId());
            project.setPlatformName(platform.getName());
        }

        jdbcManyRelationsHelper.fillAll( project );
        List<Contract> contracts = contractDAO.getByProjectId(id);

        if (CollectionUtils.isNotEmpty(contracts)) {
            project.setContracts(contracts.stream().map(contract -> new EntityOption(contract.getNumber(), contract.getId())).collect(toList()));
        }

        return ok(Project.fromCaseObject(project));
    }

    @Override
    public Result<ProjectInfo> getProjectInfo(AuthToken token, Long id) {
        CaseObject projectFromDb = caseObjectDAO.get(id);

        if (projectFromDb == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(projectFromDb, "locations");

        ProjectInfo project = ProjectInfo.fromCaseObject(projectFromDb);
        return ok(project);
    }

    @Override
    @Transactional
    public Result<Project> saveProject(AuthToken token, Project project ) {

        CaseObject caseObject = caseObjectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( caseObject );

        Project oldStateProject = Project.fromCaseObject(caseObject);

        if (!Objects.equals(project.getCustomer(), caseObject.getInitiatorCompany())) {
            return error(En_ResultStatus.NOT_ALLOWED_CHANGE_PROJECT_COMPANY);
        }

        caseObject.setName( project.getName() );
        caseObject.setInfo( project.getDescription() );
        caseObject.setStateId( project.getState().getId() );
        caseObject.setManagerId( CollectionUtils.stream( project.getTeam() )
                .filter(personProjectMemberView -> personProjectMemberView.getRole().getId() == En_DevUnitPersonRoleType.HEAD_MANAGER.getId())
                .map(PersonShortView::getId)
                .findFirst()
                .orElse(null)
        );
        caseObject.setPauseDate( project.getPauseDate() );

        caseObject.setTechnicalSupportValidity(project.getTechnicalSupportValidity());

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

        caseObject.setProjectSlas(project.getProjectSlas());

        jdbcManyRelationsHelper.persist(caseObject, "projectSlas");

        try {
            updateTeam( caseObject, project.getTeam() );
            updateLocations( caseObject, project.getRegion() );
            updateProducts( caseObject, project.getProducts() );
        } catch (Throwable e) {
            log.error("error during save project when update one of following parameters: team, location, or products; {}", e.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        caseObjectDAO.merge( caseObject );

        CaseObject updatedCaseObject = caseObjectDAO.get(project.getId());
        jdbcManyRelationsHelper.fillAll(updatedCaseObject);
        Project newStateProject = Project.fromCaseObject(updatedCaseObject);

        return ok(project).publishEvent(new ProjectUpdateEvent(this, oldStateProject, newStateProject, token.getPersonId()));
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

        project.setId(id);

        jdbcManyRelationsHelper.persist(caseObject, "projectSlas");

        try {
            updateTeam(caseObject, project.getTeam());
            updateLocations(caseObject, project.getRegion());
            updateProducts(caseObject, project.getProducts());
        } catch (Throwable e) {
            log.error("error during create project when set one of following parameters: team, location, or products; {}", e.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
        caseObjectDAO.merge( caseObject );

        Result addLinksResult = ok();

        for (CaseLink caseLink : CollectionUtils.emptyIfNull(project.getLinks())) {
            caseLink.setCaseId(caseObject.getId());
            Result currentResult = caseLinkService.createLink(token, caseLink, false);
            if (currentResult.isError()) addLinksResult = currentResult;
        }

        project.setCreator(personDAO.get(project.getCreatorId()));

        ProjectCreateEvent projectCreateEvent = new ProjectCreateEvent(this, token.getPersonId(), project.getId());

        return new Result<>(En_ResultStatus.OK, project, (addLinksResult.isOk() ? null : SOME_LINKS_NOT_SAVED), Collections.singletonList(projectCreateEvent));
    }

    private CaseObject createCaseObjectFromProjectInfo(Project project) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.PROJECT));
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setCreated(project.getCreated() == null ? new Date() : project.getCreated());
        caseObject.setStateId(project.getState().getId());
        caseObject.setCreatorId(project.getCreatorId());
        caseObject.setName(project.getName());
        caseObject.setInfo(project.getDescription());
        caseObject.setManagerId(project.getLeader() == null ? null : project.getLeader().getId());
        caseObject.setTechnicalSupportValidity(project.getTechnicalSupportValidity());
        caseObject.setProjectSlas(project.getProjectSlas());
        caseObject.setPauseDate( project.getPauseDate() );

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
    public Result<Boolean> removeProject( AuthToken token, Long projectId) {

        CaseObject caseObject = caseObjectDAO.get(projectId);

        if (caseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        caseObject.setDeleted(true);
        boolean result = caseObjectDAO.partialMerge(caseObject, "deleted");

        caseLinkService.getLinks(token, caseObject.getId()).getData()
                        .forEach(caseLink ->
                                caseLinkService.deleteLink(token, caseLink.getId()));

        return ok(result);
    }


    @Override
    public Result<SearchResult<Project>> projects(AuthToken token, ProjectQuery query) {
        CaseQuery caseQuery = query.toCaseQuery(token.getPersonId());

        SearchResult<CaseObject> projects = caseObjectDAO.getSearchResult(caseQuery);

        jdbcManyRelationsHelper.fill(projects.getResults(), "members");
//        jdbcManyRelationsHelper.fill(projects.getResults(), "products");
        jdbcManyRelationsHelper.fill(projects.getResults(), "locations");

        SearchResult<Project> result = new SearchResult<>(
                projects.getResults().isEmpty() ?
                        new ArrayList<>()
                        : projects.getResults().stream().map(Project::fromCaseObject).collect(toList()),
                projects.getTotalCount());
        return ok(result);
    }

    @Override
    public Result<List<EntityOption>> listOptionProjects(AuthToken token, ProjectQuery query) {
        CaseQuery caseQuery = query.toCaseQuery(token.getPersonId());
        List<CaseObject> projects = caseObjectDAO.listByQuery(caseQuery);

        List<EntityOption> result = projects.stream()
                .map(CaseObject::toEntityOption).collect(toList());
        return ok(result);
    }

    @Override
    public Result<List<ProjectInfo>> listInfoProjects(AuthToken token, ProjectQuery query) {
        CaseQuery caseQuery = query.toCaseQuery(token.getPersonId());
        List<CaseObject> projects = caseObjectDAO.listByQuery(caseQuery);

        jdbcManyRelationsHelper.fill(projects, "products");

        List<ProjectInfo> result = projects.stream()
                .map(ProjectInfo::fromCaseObject).collect(toList());
        return ok(result);
    }

    private void updateTeam(CaseObject caseObject, List<PersonProjectMemberView> team) {

        List<PersonProjectMemberView> toAdd = listOf(team);
        List<Long> toRemove = new ArrayList<>();
        List<En_DevUnitPersonRoleType> projectRoles = En_DevUnitPersonRoleType.getProjectRoles();

        if (caseObject.getMembers() != null) {
            for (CaseMember member : caseObject.getMembers()) {
                if (!projectRoles.contains(member.getRole())) {
                    continue;
                }
                int nPos = toAdd.indexOf(PersonProjectMemberView.fromFullNamePerson(member.getMember(), member.getRole()));
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
        if ( location == null ) {
            return;
        }

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

        jdbcManyRelationsHelper.fillAll( project );

        List< CaseLocation > locations = project.getLocations();
        if ( locations == null || locations.isEmpty() ) {
            handler.accept( null );
            return;
        }

        locations.forEach( ( location ) -> {
            handler.accept( location.getLocation() );
        } );
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

    private LocationQuery makeLocationQuery( ProjectQuery query, boolean isSortByFilter ) {
        LocationQuery locationQuery = new LocationQuery();
        locationQuery.setType(En_LocationType.REGION);
        locationQuery.setSearchString(query.getSearchString());
        locationQuery.setDistrictIds(query.getDistrictIds());
        if (isSortByFilter) {
            locationQuery.setSortField(query.getSortField());
            locationQuery.setSortDir(query.getSortDir());
        } else {
            locationQuery.setSortField(En_SortField.name);
            locationQuery.setSortDir(En_SortDir.ASC);
        }
        return locationQuery;
    }
}
