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
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
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

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.access.ProjectAccessUtil.canAccessProject;
import static ru.protei.portal.core.access.ProjectAccessUtil.getProjectAccessType;
import static ru.protei.portal.core.model.dict.En_ExpiringProjectTSVPeriod.*;
import static ru.protei.portal.core.model.dict.En_SortField.project_head_manager;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.dto.Project.Fields.*;


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
    DevUnitDAO devUnitDAO;
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
    @Autowired
    CompanyImportanceItemDAO companyImportanceItemDAO;
    @Autowired
    HistoryService historyService;
    @Autowired
    HistoryDAO historyDAO;
    @Autowired
    CaseStateDAO caseStateDAO;

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

        jdbcManyRelationsHelper.fill( project, PROJECT_MEMBERS );

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

        Optional<Long> previousProjectStateIdOptional =
                ofNullable(historyDAO.getLastHistory(projectId, En_HistoryType.CASE_STATE))
                        .map(History::getOldId);

        if (!previousProjectStateIdOptional.isPresent()) {
            log.info( "onPauseTimeNotification(): Previous project state is id null. No need to reset state");
            return;
        }

        Long previousProjectStateId = previousProjectStateIdOptional.get();

        log.info( "onPauseTimeNotification(): Previous project state id is {}. Need to reset state", previousProjectStateId);

        project.setStateId(previousProjectStateId);

        Long systemUserId = config.data().getCommonConfig().getSystemUserId();

        updateCaseObjectPart(createFakeToken(systemUserId == null ? project.getCreatorId() : systemUserId), project);
    }

    @Override
    public Result< List< RegionInfo > > listRegions( AuthToken token, ProjectQuery query ) {
        List< Location > regions = locationDAO.listByQuery( makeLocationQuery(query, true ));
        return ok(regions.stream().map(Location::toRegionInfo).collect(toList()));
    }

    @Override
    public Result<Project> getProject(AuthToken token, Long id ) {

        Project project = projectDAO.get( id );

        if (project == null) {
            return error(En_ResultStatus.NOT_FOUND, "Project was not found");
        }

        jdbcManyRelationsHelper.fillAll( project );
        project.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(project.getId())));
        project.setProducts(new HashSet<>(devUnitDAO.getProjectProducts(project.getId())));
        project.getProducts().forEach(product -> product.setProductDirections(new HashSet<>(devUnitDAO.getProductDirections(product.getId()))));
        project.setContracts(CollectionUtils.toList(removeCancelledContracts(contractDAO.getByProjectId(id)), Contract::toEntityOption));
        project.setPlatforms(CollectionUtils.toList(platformDAO.getByProjectId(id), Platform::toEntityOption));

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

        jdbcManyRelationsHelper.fill(projectFromDb, PROJECT_LOCATIONS);
        jdbcManyRelationsHelper.fill(projectFromDb, PROJECT_MEMBERS);
        projectFromDb.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(projectFromDb.getId())));
        projectFromDb.setProducts(new HashSet<>(devUnitDAO.getProjectProducts(projectFromDb.getId())));

        final Set<DevUnit> products = projectFromDb.getProducts();
        if (isNotEmpty(products)) {
            products.forEach(product -> product.setProductDirections(new HashSet<>(emptyIfNull(devUnitDAO.getProductDirections(product.getId())))));
        }

        ProjectInfo project = ProjectInfo.fromProject(projectFromDb);
        return ok(project);
    }

    @Override
    @Transactional
    public Result<Project> saveProject(AuthToken token, Project project ) {
        if (!validateFields(project)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Project projectFormDB = projectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( projectFormDB );
        projectFormDB.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(project.getId())));
        projectFormDB.setProducts(new HashSet<>(devUnitDAO.getProjectProducts(project.getId())));

        List<CompanyImportanceItem> sortedImportanceLevels
                = companyImportanceItemDAO.getSortedImportanceLevels(project.getCustomerId());

        Project oldStateProject = projectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( oldStateProject );
        oldStateProject.setProjectSlas(getSortedSla(oldStateProject.getProjectSlas(), sortedImportanceLevels));
        oldStateProject.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(project.getId())));
        oldStateProject.setProducts(new HashSet<>(devUnitDAO.getProjectProducts(project.getId())));

        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_EDIT, oldStateProject.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!Objects.equals(project.getCustomer(), projectFormDB.getCustomer())) {
            return error(En_ResultStatus.NOT_ALLOWED_CHANGE_PROJECT_COMPANY);
        }

        projectFormDB.setTechnicalSupportValidity(project.getTechnicalSupportValidity());
        projectFormDB.setWorkCompletionDate(project.getWorkCompletionDate());
        projectFormDB.setPurchaseDate(project.getPurchaseDate());

        if (project.getCustomerType() != null)
            projectFormDB.setCustomerType(project.getCustomerType());

        projectFormDB.setProjectSlas(project.getProjectSlas());
        jdbcManyRelationsHelper.persist(projectFormDB, PROJECT_SLAS);

        projectFormDB.setSubcontractors(project.getSubcontractors());
        jdbcManyRelationsHelper.persist(projectFormDB, PROJECT_SUBCONTRACTORS);

        jdbcManyRelationsHelper.persist(project, PROJECT_PLANS);

        try {
            updateDevUnits( projectFormDB, emptyIfNull(projectFormDB.getProducts()),  emptyIfNull(project.getProducts()) );
            updateDevUnits( projectFormDB, emptyIfNull(projectFormDB.getProductDirections()), emptyIfNull(project.getProductDirections()) );
        } catch (Throwable e) {
            log.error("saveProject(): error during save project when update products;", e);
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        boolean merge = projectDAO.merge(projectFormDB);

        if (!merge) {
            log.error("saveProject(): failed to merge project. Rollback transaction");
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        merge = updateCaseObjectPart(token, project);

        if (!merge) {
            log.error("saveProject(): failed to merge caseObject. Rollback transaction");
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        Project newStateProject = projectDAO.get(project.getId());
        jdbcManyRelationsHelper.fillAll(newStateProject);
        newStateProject.setProjectSlas(getSortedSla(newStateProject.getProjectSlas(), sortedImportanceLevels));
        newStateProject.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(project.getId())));
        newStateProject.setProducts(new HashSet<>(devUnitDAO.getProjectProducts(project.getId())));

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

        jdbcManyRelationsHelper.persist(project, PROJECT_SLAS);
        jdbcManyRelationsHelper.persist(project, PROJECT_PLANS);
        jdbcManyRelationsHelper.persist(project, PROJECT_SUBCONTRACTORS);

        try {
            updateTeam(caseObject, project.getTeam());
            updateLocations(caseObject,  project.getRegion());
            updateDevUnits( project, new HashSet<>(), emptyIfNull(project.getProducts()) );
            updateDevUnits( project, new HashSet<>(), emptyIfNull(project.getProductDirections()) );
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

        long stateId = project.getStateId();

        addStateHistory(token, projectId, stateId, caseStateDAO.get(stateId).getState());

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

        jdbcManyRelationsHelper.fill(project, PROJECT_MEMBERS);
        if (!canAccessProject(policyService, token, En_Privilege.PROJECT_REMOVE, project.getTeam())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        caseObject.setDeleted(true);
        boolean result = caseObjectDAO.partialMerge(caseObject, "deleted");

        if (result) {
            caseLinkService.getLinks(token, caseObject.getId())
                .ifOk(links -> caseLinkService.deleteLinks(token, links, En_CaseType.PROJECT));
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

        jdbcManyRelationsHelper.fill(projects.getResults(), PROJECT_MEMBERS);
        jdbcManyRelationsHelper.fill(projects.getResults(), PROJECT_LOCATIONS);
        projects.forEach(project -> {
                    project.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(project.getId())));
                    project.setProducts(new HashSet<>(devUnitDAO.getProjectProducts(project.getId())));
                }
        );

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

        projects.forEach(project -> {
            project.setProducts(new HashSet<>(devUnitDAO.getProjectProducts(project.getId())));
            project.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(project.getId())));
            jdbcManyRelationsHelper.fill(project,PROJECT_MEMBERS);
        });

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

    private List<Long> collectCompanyIds(ProjectQuery projectQuery) {
        List<Long> companyIds = new ArrayList<>();
        companyIds.addAll(emptyIfNull(projectQuery.getInitiatorCompanyIds()));
        return companyIds;
    }

    private List<Contract> removeCancelledContracts(List<Contract> contracts) {
        return stream(contracts).filter(contract -> !Objects.equals(En_ContractState.CANCELLED, contract.getState())).collect(toList());
    }

    private boolean updateCaseObjectPart(AuthToken token, Project project) {
        CaseObject caseObject = caseObjectDAO.get( project.getId() );
        jdbcManyRelationsHelper.fillAll( caseObject );

        long oldStateId = caseObject.getStateId();
        long newStateId = project.getStateId();

        caseObject = createCaseObjectFromProject(caseObject, project);

        try {
            updateTeam( caseObject, project.getTeam() );
            updateLocations( caseObject, project.getRegion() );
        } catch (Throwable e) {
            log.error("saveProject(): error during save project when update team or location;", e);
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        if (!project.getStateId().equals(CrmConstants.State.PAUSED)) {
            caseObject.setPauseDate(null);
        }

        boolean isMergeSuccessful = caseObjectDAO.merge(caseObject);

        boolean isStateChanged = oldStateId != newStateId;

        if (isMergeSuccessful && isStateChanged) {
            changeStateHistory(token, project.getId(), oldStateId, caseStateDAO.get(oldStateId).getState(), newStateId, caseStateDAO.get(newStateId).getState());
        }

        return isMergeSuccessful;
    }

    private List<ProjectSla> getSortedSla(List<ProjectSla> unsortedSla, List<CompanyImportanceItem> companyImportanceItems) {
        return unsortedSla
                .stream()
                .sorted(Comparator.comparingInt(projectSla -> getProjectSlaOrderNumber(projectSla, companyImportanceItems)))
                .collect(toList());
    }

    private Integer getProjectSlaOrderNumber(ProjectSla projectSla, List<CompanyImportanceItem> companyImportanceItems) {
        return companyImportanceItems
                .stream()
                .filter(companyImportanceItem -> companyImportanceItem.getImportanceLevelId().equals(projectSla.getImportanceLevelId()))
                .map(CompanyImportanceItem::getOrderNumber)
                .findAny()
                .orElse(0);
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

        caseObject.setStateId(project.getStateId());

        caseObject.setName(project.getName());
        caseObject.setInfo(project.getDescription());
        caseObject.setManagerId(project.getLeader() == null ? null : project.getLeader().getId());
        caseObject.setPauseDate( project.getPauseDate() );

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
        List<En_PersonRoleType> projectRoles = En_PersonRoleType.getProjectRoles();

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

    private void updateDevUnits(Project project, Set<DevUnit> oldDevUnits , Set<DevUnit> newDevUnits) {
        if (isEmpty(newDevUnits)) {
            return;
        }

        Set<DevUnit> toDelete = new HashSet<>(oldDevUnits);
        Set<DevUnit> toCreate = new HashSet<>(newDevUnits);
        toCreate.removeAll(oldDevUnits);
        toDelete.removeAll(newDevUnits);

        ProjectToProduct projectToDevUnit = new ProjectToProduct(project.getId(), null);

        toDelete.forEach(du -> {
            projectToDevUnit.setProductId(du.getId());
            projectToProductDAO.removeByKey(projectToDevUnit);
        });
        toCreate.forEach(du -> {
            projectToDevUnit.setProductId(du.getId());
            projectToProductDAO.persist(projectToDevUnit);
        });
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

    private Result<Long> addStateHistory(AuthToken authToken, Long projectId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, projectId, En_HistoryAction.ADD, En_HistoryType.CASE_STATE, null, null, stateId, stateName);
    }

    private Result<Long> changeStateHistory(AuthToken token, Long projectId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(token, projectId, En_HistoryAction.CHANGE, En_HistoryType.CASE_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private AuthToken createFakeToken(Long personId) {
        AuthToken result = new AuthToken("0");
        result.setPersonId(personId);

        return result;
    }
}
