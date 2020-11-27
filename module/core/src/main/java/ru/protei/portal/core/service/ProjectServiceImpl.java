package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.dto.ProjectTSVReportInfo;
import ru.protei.portal.core.model.dto.RegionInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.DateRangeUtils;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.portal.tools.ListByFeatureIterator;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.access.ProjectAccessUtil.canAccessProject;
import static ru.protei.portal.core.access.ProjectAccessUtil.getProjectAccessType;
import static ru.protei.portal.core.model.dict.En_ExpiringProjectTSVPeriod.*;
import static ru.protei.portal.core.model.dict.En_SortField.project_head_manager;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;


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
    ProjectTechnicalSupportValidityReportInfoDAO projectTSVReportInfoDAO;
    @Autowired
    PortalScheduleTasks scheduledTasksService;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    PortalConfig config;

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

        List< Project > projects = projectDAO.listByQuery( query );
        projects.forEach( ( project ) -> {
            iterateAllLocations( project, ( location ) -> {
                applyCaseToProjectInfo( project, location, regionToProjectMap );
            } );
        } );

        return ok(regionToProjectMap );
    }

    @Override
    public Result<Project> getProject(AuthToken token, Long id ) {

        Project project = projectDAO.get( id );

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

        jdbcManyRelationsHelper.fill(project, Project.Fields.PROJECT_PLANS);

        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_VIEW, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        return ok(project);
    }

    @Override
    public Result<ProjectInfo> getProjectInfo(AuthToken token, Long id) {
        Project projectFromDb = projectDAO.get(id);

        if (projectFromDb == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(projectFromDb, "locations");
        jdbcManyRelationsHelper.fill(projectFromDb, "members");

        ProjectInfo project = ProjectInfo.fromProject(projectFromDb);
        return ok(project);
    }

    @Override
    @Transactional
    public Result<Project> saveProject(AuthToken token, Project project ) {
        if (!validateFields(project)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CaseObject caseObject = caseObjectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( caseObject );

        Project projectFormDB = projectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( projectFormDB );

        Project oldStateProject = projectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( oldStateProject );

        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_EDIT, oldStateProject.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!Objects.equals(project.getCustomer(), projectFormDB.getCustomer())) {
            return error(En_ResultStatus.NOT_ALLOWED_CHANGE_PROJECT_COMPANY);
        }

        caseObject = createCaseObjectFromProject(caseObject, project);

        projectFormDB.setTechnicalSupportValidity(project.getTechnicalSupportValidity());
        projectFormDB.setWorkCompletionDate(project.getWorkCompletionDate());
        projectFormDB.setPurchaseDate(project.getPurchaseDate());

        if (project.getCustomerType() != null)
            projectFormDB.setCustomerType(project.getCustomerType());

        projectFormDB.setProjectSlas(project.getProjectSlas());
        jdbcManyRelationsHelper.persist(projectFormDB, "projectSlas");

        jdbcManyRelationsHelper.persist(project, Project.Fields.PROJECT_PLANS);

        try {
            updateTeam( caseObject, project.getTeam() );
            updateLocations( caseObject, project.getRegion() );
            updateProducts( projectFormDB, project.getProducts() );
        } catch (Throwable e) {
            log.error("saveProject(): error during save project when update one of following parameters: team, location, or products;", e);
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        boolean merge = projectDAO.merge(projectFormDB);

        if (!merge) {
            log.error("saveProject(): failed to merge project. Rollback transaction");
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        merge = caseObjectDAO.merge( caseObject );

        if (!merge) {
            log.error("saveProject(): failed to merge caseObject. Rollback transaction");
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        Project newStateProject = projectDAO.get(project.getId());
        jdbcManyRelationsHelper.fillAll(newStateProject);

        return ok(project).publishEvent(new ProjectUpdateEvent(this, oldStateProject, newStateProject, token.getPersonId()));
    }

    @Override
    @Transactional
    public Result<Project> createProject(AuthToken token, Project project) {
        if (!validateFields(project)) {
            log.warn("createProject(): project not valid. project={}", project);
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_CREATE, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        CaseObject caseObject = createCaseObjectFromProject(null, project);

        Long caseObjectId = caseObjectDAO.persist(caseObject);
        if (caseObjectId == null) {
            log.warn("createProject(): caseObject not created. project={}", project);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }
        project.setId(caseObjectId);

        Long projectId = projectDAO.persist(project);

        if (projectId == null) {
            log.warn("createProject(): project not created. project={}", project);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        jdbcManyRelationsHelper.persist(project, "projectSlas");
        jdbcManyRelationsHelper.persist(project, Project.Fields.PROJECT_PLANS);

        try {
            updateTeam(caseObject, project.getTeam());
            updateLocations(caseObject,  project.getRegion());
            updateProducts(project, project.getProducts());
        } catch (Throwable e) {
            log.error("createProject(): error during create project when set one of following parameters: team, location, or products; {}", e.getMessage());
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        boolean merged = caseObjectDAO.merge(caseObject);
        if (!merged) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        List<CaseLink> links = emptyIfNull(project.getLinks());

        links.forEach(link -> link.setCaseId(projectId));

        Result<List<CaseLink>> createdLinksResult
                = caseLinkService.createLinks(token, links, En_CaseType.PROJECT);

        ProjectCreateEvent projectCreateEvent = new ProjectCreateEvent(this, token.getPersonId(), project.getId());

        return new Result<>(En_ResultStatus.OK, project, createdLinksResult.getMessage(), Collections.singletonList(projectCreateEvent));
    }

    @Override
    @Transactional
    public Result<Long> removeProject(AuthToken token, Long projectId) {

        Project project = projectDAO.get(projectId);
        CaseObject caseObject = caseObjectDAO.get(projectId);
        if (project == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(project, "members");
        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_REMOVE, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        caseObject.setDeleted(true);
        boolean result = caseObjectDAO.partialMerge(caseObject, "deleted");

        if (result) {
            caseLinkService.getLinks(token, caseObject.getId())
                .ifOk(links -> caseLinkService.deleteLinks(token, links));
        }
        return ok(projectId);
    }

    @Override
    public Result<SearchResult<Project>> projects(AuthToken token, ProjectQuery query) {

        En_ProjectAccessType accessType = getProjectAccessType(policyService, token, En_Privilege.PROJECT_VIEW);
        if (accessType == En_ProjectAccessType.SELF_PROJECTS) {
            query.setMemberId(token.getPersonId());
        }

        SearchResult<Project> projects = projectDAO.getSearchResult(query);

        jdbcManyRelationsHelper.fill(projects.getResults(), "members");
        jdbcManyRelationsHelper.fill(projects.getResults(), "products");
        jdbcManyRelationsHelper.fill(projects.getResults(), "locations");

        return ok(projects);
    }

    @Override
    public Result<List<EntityOption>> listOptionProjects(AuthToken token, ProjectQuery query) {
        List<Project> projects = projectDAO.listByQuery(query);

        List<EntityOption> result = projects.stream()
                .map(Project::toEntityOption).collect(toList());
        return ok(result);
    }

    @Override
    public Result<List<ProjectInfo>> listInfoProjects(AuthToken token, ProjectQuery query) {
        List<Project> projects = projectDAO.listByQuery(query);

        jdbcManyRelationsHelper.fill(projects, "products");

        List<ProjectInfo> result = projects.stream()
                .map(ProjectInfo::fromProject).collect(toList());
        return ok(result);
    }

    @Override
    public Result<List<PersonProjectMemberView>> getProjectTeam(AuthToken token, Long projectId) {
        if (projectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Project project = new Project();
        project.setId(projectId);

        project.setMembers(caseMemberDAO.listByCaseId(projectId));
        List<PersonProjectMemberView> team = project.getTeam();
        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_VIEW, team)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        return ok(team);
    }

    @Override
    public Result<PersonShortView> getProjectLeader(AuthToken authToken, Long projectId) {
        if (projectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return caseMemberDAO.getLeaders(projectId)
                .stream()
                .findFirst()
                .map(leader -> new PersonShortView(leader.getMember()) )
                .map(Result::ok)
                .orElse(ok(null));
    }

    @Override
    public Result<Boolean> notifyExpiringProjectTechnicalSupportValidity(LocalDate now) {
        log.info("notifyExpiringProjectTechnicalSupportValidity(): start");

        Map<En_ExpiringProjectTSVPeriod, Interval> expiringPeriodToIntervals = makeProjectTSVIntervals(now);
        CollectionUtils.stream(createProjectTSVReportInfoListsSeparateByHeadManagerIterator(expiringPeriodToIntervals))
                .map(list -> createExpiringProjectTSVNotificationEvent(list, expiringPeriodToIntervals))
                .forEach(publisherService::publishEvent);

        log.info("notifyExpiringProjectTechnicalSupportValidity(): done");
        return ok(true);
    }

    private Map<En_ExpiringProjectTSVPeriod, Interval> makeProjectTSVIntervals(LocalDate now) {
        return Stream.of(DAYS_7, DAYS_14, DAYS_30)
                .collect(Collectors.toMap(
                        Function.identity(),
                        period -> DateRangeUtils.makeIntervalWithOffset(now, period.getDays())));
    }

    private ListByFeatureIterator<ProjectTSVReportInfo, Long> createProjectTSVReportInfoListsSeparateByHeadManagerIterator(
                        Map<En_ExpiringProjectTSVPeriod, Interval> intervalsMap) {
        int limit = config.data().reportConfig().getChunkSize();
        ProjectQuery query = getExpiringProjectTSVQuery(intervalsMap, limit);
        return new ListByFeatureIterator<>(
                () -> {
                    SearchResult<ProjectTSVReportInfo> searchResult =
                            projectTSVReportInfoDAO.getSearchResultByQuery(query);
                    query.setOffset(query.getOffset() + limit);
                    return searchResult.getResults();
                },
                ProjectTSVReportInfo::getHeadManagerId
        );
    }

    private ProjectQuery getExpiringProjectTSVQuery(Map<En_ExpiringProjectTSVPeriod, Interval> intervalsMap, int limit) {
        ProjectQuery query = new ProjectQuery();
        query.setSortField(project_head_manager);
        query.setTechnicalSupportExpiresInDays(new ArrayList<>(intervalsMap.values()));
        query.setOffset(0);
        query.setLimit(limit);
        return query;
    }

    private ExpiringProjectTSVNotificationEvent createExpiringProjectTSVNotificationEvent(List<ProjectTSVReportInfo> list,
                                                      Map<En_ExpiringProjectTSVPeriod, Interval> expiringPeriodToIntervals) {
        Person headManager = makeNotificationPerson(list.get(0).getHeadManagerId());
        Map<En_ExpiringProjectTSVPeriod, List<ProjectTSVReportInfo>> infos = list.stream()
                .collect(Collectors.groupingBy(info -> groupingByExpiringTSVPeriod(info, expiringPeriodToIntervals)));
        return new ExpiringProjectTSVNotificationEvent(this, headManager, infos);
    }

    private Person makeNotificationPerson(long id) {
        Person person = new Person();
        person.setId(id);
        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
        return person;
    }

    private En_ExpiringProjectTSVPeriod groupingByExpiringTSVPeriod (ProjectTSVReportInfo info, Map<En_ExpiringProjectTSVPeriod, Interval> intervals) {
        if (isInInterval(intervals.get(DAYS_7), info.getTechnicalSupportValidity())) {
            return DAYS_7;
        } else if (isInInterval(intervals.get(DAYS_14), info.getTechnicalSupportValidity())) {
            return DAYS_14;
        } else {
            return DAYS_30;
        }
    }

    private boolean isInInterval(Interval interval, Date date) {
        return interval.from.equals(date) ||
                (interval.from.before(date) && date.before(interval.to));
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

    private CaseObject createCaseObjectFromProject(CaseObject caseObject, Project project) {
        if (caseObject == null){
            caseObject = new CaseObject();
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.PROJECT));
            caseObject.setType(En_CaseType.PROJECT);
            caseObject.setCreated(project.getCreated() == null ? new Date() : project.getCreated());
            caseObject.setCreatorId(project.getCreatorId());
        } else {
            caseObject.setModified(new Date());
        }

        caseObject.setId(project.getId());

        caseObject.setStateId(project.getState().getId());

        caseObject.setName(project.getName());
        caseObject.setInfo(project.getDescription());
        caseObject.setManagerId(project.getLeader() == null ? null : project.getLeader().getId());
        caseObject.setPauseDate( project.getPauseDate() );

        caseObject.setProductId(project.getProductDirectionEntityOption() == null ? null :  project.getProductDirectionEntityOption().getId());

        if (project.getCustomer() == null) {
            caseObject.setInitiatorCompany(null);
        } else {
            caseObject.setInitiatorCompanyId(project.getCustomer().getId());
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
                int nPos = toAdd.indexOf(new PersonProjectMemberView(member.getMember(), member.getRole()));
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

    private void updateProducts(Project project, Set<DevUnit> products) {
        if (products == null)
            return;

        Set<DevUnit> oldProducts = project.getProducts() == null ? new HashSet<>() : project.getProducts();
        Set<DevUnit> newProducts = products == null ? new HashSet<>() : products;

        Set<DevUnit> toDelete = new HashSet<>(oldProducts);
        Set<DevUnit> toCreate = new HashSet<>(newProducts);
        toCreate.removeAll(oldProducts);
        toDelete.removeAll(newProducts);

        ProjectToProduct projectToProduct = new ProjectToProduct(project.getId(), null);

        toDelete.forEach(du -> {
            projectToProduct.setProductId(du.getId());
            projectToProductDAO.removeByKey(projectToProduct);
        });
        toCreate.forEach(du -> {
            projectToProduct.setProductId(du.getId());
            projectToProductDAO.persist(projectToProduct);
        });

        project.setProducts(newProducts);
    }

    private void iterateAllLocations( Project project, Consumer< Location > handler ) {
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

    private void applyCaseToProjectInfo( Project project, Location location, Map< String, List<Project> > projects ) {

        String locationName = ""; // name for empty location
        if ( location != null ) {
            locationName = location.getName();
        }

        List<Project> projectInfos = projects.computeIfAbsent(locationName, ignore -> new ArrayList<>());

        projectInfos.add( project );
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
