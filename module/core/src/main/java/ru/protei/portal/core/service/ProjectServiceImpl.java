package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.dto.RegionInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
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
import static ru.protei.portal.core.access.ProjectAccessUtil.canAccessProject;
import static ru.protei.portal.core.access.ProjectAccessUtil.getProjectAccessType;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;
import static ru.protei.portal.core.model.view.PersonProjectMemberView.fromFullNamePerson;

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
            log.info("onPauseTimeNotification(): Ignore notification: pause date was changed: old {} new {}",
                    simpleDateFormat.format( pauseDate ),
                    project.getPauseDate() == null ? null : simpleDateFormat.format( project.getPauseDate() )
            );
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
        jdbcManyRelationsHelper.fill(persons, Person.Fields.CONTACT_ITEMS);

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

        CaseObject projectCO = caseObjectDAO.get( id );

        if (projectCO == null) {
            return error(En_ResultStatus.NOT_FOUND, "Project was not found");
        }

        Platform platform = platformDAO.getByProjectId(id);

        if (platform != null) {
            projectCO.setPlatformId(platform.getId());
            projectCO.setPlatformName(platform.getName());
        }

        jdbcManyRelationsHelper.fillAll( projectCO );
        List<Contract> contracts = contractDAO.getByProjectId(id);

        if (CollectionUtils.isNotEmpty(contracts)) {
            projectCO.setContracts(contracts.stream().map(contract -> new EntityOption(contract.getNumber(), contract.getId())).collect(toList()));
        }

        Project project = Project.fromCaseObject(projectCO);

        jdbcManyRelationsHelper.fill(project, Project.Fields.PROJECT_PLANS);

        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_VIEW, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        return ok(project);
    }

    @Override
    public Result<ProjectInfo> getProjectInfo(AuthToken token, Long id) {
        CaseObject projectFromDb = caseObjectDAO.get(id);

        if (projectFromDb == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(projectFromDb, "locations");

        jdbcManyRelationsHelper.fill(projectFromDb, "members");
        Project project = Project.fromCaseObject(projectFromDb);
        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_VIEW, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        ProjectInfo projectInfo = ProjectInfo.fromCaseObject(projectFromDb);
        return ok(projectInfo);
    }

    @Override
    @Transactional
    public Result<Project> saveProject(AuthToken token, Project project ) {
        if (!validateFields(project)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CaseObject caseObject = caseObjectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( caseObject );

        Project oldStateProject = Project.fromCaseObject(caseObject);
        jdbcManyRelationsHelper.fill(oldStateProject, Project.Fields.PROJECT_PLANS);

        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_EDIT, oldStateProject.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

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

        jdbcManyRelationsHelper.persist(project, Project.Fields.PROJECT_PLANS);

        try {
            updateTeam( caseObject, project.getTeam() );
            updateLocations( caseObject, project.getRegion() );
            updateProducts( caseObject, project.getProducts() );
        } catch (Throwable e) {
            log.error("error during save project when update one of following parameters: team, location, or products; {}", e.getMessage());
            throw new ResultStatusException(En_ResultStatus.INTERNAL_ERROR);
        }

        boolean merged = caseObjectDAO.merge(caseObject);
        if (!merged) {
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
        }

        CaseObject updatedCaseObject = caseObjectDAO.get(project.getId());
        jdbcManyRelationsHelper.fillAll(updatedCaseObject);
        Project newStateProject = Project.fromCaseObject(updatedCaseObject);
        jdbcManyRelationsHelper.fill(newStateProject, Project.Fields.PROJECT_PLANS);

        return ok(project).publishEvent(new ProjectUpdateEvent(this, oldStateProject, newStateProject, token.getPersonId()));
    }

    @Override
    @Transactional
    public Result<Project> createProject(AuthToken token, Project project) {
        if (!validateFields(project)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_CREATE, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        CaseObject caseObject = createCaseObjectFromProjectInfo(project);

        Long id = caseObjectDAO.persist(caseObject);
        if (id == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        project.setId(id);

        jdbcManyRelationsHelper.persist(caseObject, "projectSlas");
        jdbcManyRelationsHelper.persist(project, Project.Fields.PROJECT_PLANS);

        try {
            updateTeam(caseObject, project.getTeam());
            updateLocations(caseObject, project.getRegion());
            updateProducts(caseObject, project.getProducts());
        } catch (Throwable e) {
            log.error("error during create project when set one of following parameters: team, location, or products; {}", e.getMessage());
            throw new ResultStatusException(En_ResultStatus.INTERNAL_ERROR);
        }

        boolean merged = caseObjectDAO.merge(caseObject);
        if (!merged) {
            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
        }

        Result addLinksResult = ok();

        for (CaseLink caseLink : CollectionUtils.emptyIfNull(project.getLinks())) {
            caseLink.setCaseId(caseObject.getId());
            Result currentResult = caseLinkService.createLink(token, caseLink, caseObject.getType());
            if (currentResult.isError()) addLinksResult = currentResult;
        }

        Person creator = personDAO.get(project.getCreatorId());
        jdbcManyRelationsHelper.fill(creator, Person.Fields.CONTACT_ITEMS);
        project.setCreator(creator);

        ProjectCreateEvent projectCreateEvent = new ProjectCreateEvent(this, token.getPersonId(), project.getId());

        return new Result<>(En_ResultStatus.OK, project, (addLinksResult.isOk() ? null : SOME_LINKS_NOT_SAVED), Collections.singletonList(projectCreateEvent));
    }

    @Override
    @Transactional
    public Result<Boolean> removeProject( AuthToken token, Long projectId) {

        CaseObject caseObject = caseObjectDAO.get(projectId);
        if (caseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(caseObject, "members");
        Project project = Project.fromCaseObject(caseObject);
        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_REMOVE, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        caseObject.setDeleted(true);
        boolean result = caseObjectDAO.partialMerge(caseObject, "deleted");

        if (result) {
            caseLinkService.getLinks(token, caseObject.getId())
                .ifOk(links -> links.forEach(caseLink -> {
                    caseLinkService.deleteLink(token, caseLink.getId());
                }));
        }
        return ok(result);
    }

    @Override
    public Result<SearchResult<Project>> projects(AuthToken token, ProjectQuery query) {

        En_ProjectAccessType accessType = getProjectAccessType(policyService, token, En_Privilege.PROJECT_VIEW);
        if (accessType == En_ProjectAccessType.SELF_PROJECTS) {
            query.setOnlyMineProjects(true);
        }

        CaseQuery caseQuery = query.toCaseQuery(token.getPersonId());

        SearchResult<CaseObject> projects = caseObjectDAO.getSearchResult(caseQuery);

        jdbcManyRelationsHelper.fill(projects.getResults(), "members");
        jdbcManyRelationsHelper.fill(projects.getResults(), "products");
        jdbcManyRelationsHelper.fill(projects.getResults(), "locations");

        SearchResult<Project> result = new SearchResult<>(stream(projects.getResults())
                .map(Project::fromCaseObject)
                .collect(toList()));
        return ok(result);
    }

    @Override
    public Result<List<EntityOption>> listOptionProjects(AuthToken token, ProjectQuery query) {
        CaseQuery caseQuery = query.toCaseQuery(token.getPersonId());
        List<CaseObject> projects = caseObjectDAO.listByQuery(caseQuery);

        List<EntityOption> result = stream(projects)
                .map(CaseObject::toEntityOption)
                .collect(toList());
        return ok(result);
    }

    @Override
    public Result<List<ProjectInfo>> listInfoProjects(AuthToken token, ProjectQuery query) {
        CaseQuery caseQuery = query.toCaseQuery(token.getPersonId());
        List<CaseObject> projects = caseObjectDAO.listByQuery(caseQuery);

        jdbcManyRelationsHelper.fill(projects, "products");

        List<ProjectInfo> result = stream(projects)
                .map(ProjectInfo::fromCaseObject)
                .collect(toList());
        return ok(result);
    }

    @Override
    public Result<List<PersonProjectMemberView>> getProjectTeam(AuthToken token, Long projectId) {
        if (projectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        CaseObject caseObject = new CaseObject(projectId);
        caseObject.setMembers(caseMemberDAO.listByCaseId(projectId));
        List<PersonProjectMemberView> team = Project.fromCaseObject(caseObject).getTeam();
        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_VIEW, team)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        return ok(team);
    }

    @Override
    public Result<PersonProjectMemberView> getProjectLeader(AuthToken authToken, Long projectId) {
        return caseMemberDAO.getLeaders(projectId)
                .stream()
                .findFirst()
                .map(leader -> fromFullNamePerson(leader.getMember(), En_DevUnitPersonRoleType.HEAD_MANAGER))
                .map(Result::ok)
                .orElse(ok(null));
    }

    private boolean validateFields(Project project) {
        if (project == null) {
            return false;
        }

        if (project.getLeader() == null) {
            return false;
        }

        return true;
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

    private void updateTeam(CaseObject caseObject, List<PersonProjectMemberView> team) {

        List<PersonProjectMemberView> toAdd = listOf(team);
        List<Long> toRemove = new ArrayList<>();
        List<En_DevUnitPersonRoleType> projectRoles = En_DevUnitPersonRoleType.getProjectRoles();

        if (caseObject.getMembers() != null) {
            for (CaseMember member : caseObject.getMembers()) {
                if (!projectRoles.contains(member.getRole())) {
                    continue;
                }
                int nPos = toAdd.indexOf(fromFullNamePerson(member.getMember(), member.getRole()));
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
