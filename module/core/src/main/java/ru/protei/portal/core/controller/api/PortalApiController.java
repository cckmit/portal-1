package ru.protei.portal.core.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.CaseTagInfo;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.dto.PersonInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.*;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.util.AuthUtils.authenticate;

/**
 * Севрис для  API
 */
@RestController
@RequestMapping(value = "/api", headers = "Accept=application/json")
@EnableWebMvc
public class PortalApiController {

    @Autowired
    private AuthService authService;
    @Autowired
    private SessionIdGen sidGen;
    @Autowired
    private CaseService caseService;
    @Autowired
    private CaseLinkService caseLinkService;
    @Autowired
    private CaseCommentService caseCommentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CaseTagService caseTagService;
    @Autowired
    private YtDtoFieldsMapper fieldsMapper;
    @Autowired
    PortalConfig config;


    private static final Logger log = LoggerFactory.getLogger(PortalApiController.class);

    /**
     * Получение списка обращений List<CaseShortView> по параметрам CaseApiQuery
     *
     * @param query
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/cases")
    public Result<List<CaseShortView>> getCaseList(
            @RequestBody CaseApiQuery query,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("API | getCaseList(): query={}", query);

        try {
            Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

            if (authTokenAPIResult.isError()) {
                return error(authTokenAPIResult.getStatus(), authTokenAPIResult.getMessage());
            }

            AuthToken authToken = authTokenAPIResult.getData();

            Result<SearchResult<CaseShortView>> searchList = caseService.getCaseObjects(authToken, makeCaseQuery(query));

            return searchList.map( SearchResult::getResults );

        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INCORRECT_PARAMS, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR, ex.getMessage());
        }
    }

    @PostMapping(value = "/cases/create")
    public Result<CaseObject> createCase(@RequestBody AuditableObject auditableObject,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        log.info("API | createCase(): auditableObject={}", auditableObject);

        if (!(auditableObject instanceof CaseObject)) {
            return error(En_ResultStatus.INCORRECT_PARAMS, "Incorrect AuditType");
        }

        try {
            Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

            if (authTokenAPIResult.isError()) {
                return error(authTokenAPIResult.getStatus(), authTokenAPIResult.getMessage());
            }

            AuthToken authToken = authTokenAPIResult.getData();

            CaseObjectCreateRequest caseObjectCreateRequest = new CaseObjectCreateRequest((CaseObject) auditableObject);

            Result<CaseObject> caseObjectCoreResponse = caseService.createCaseObject(
                    authToken,
                    caseObjectCreateRequest
            );

            return caseObjectCoreResponse.orElseGet( result ->
                    error( result.getStatus(),  "Service Error" ));

        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INCORRECT_PARAMS, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR, ex.getMessage());
        }
    }

    @PostMapping(value = "/cases/update")
    public Result<CaseObject> updateCase(@RequestBody AuditableObject auditableObject,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        log.info("API | updateCase(): auditableObject={}", auditableObject);

        if (!(auditableObject instanceof CaseObject)) {
            return error(En_ResultStatus.INCORRECT_PARAMS, "Incorrect AuditType");
        }

        try {
            Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

            if (authTokenAPIResult.isError()) {
                return error(authTokenAPIResult.getStatus(), authTokenAPIResult.getMessage());
            }

            AuthToken authToken = authTokenAPIResult.getData();

            CaseObject caseObject = (CaseObject) auditableObject;

            return caseService.updateCaseNameAndDescription(authToken, new CaseNameAndDescriptionChangeRequest(caseObject.getId(), caseObject.getName(), caseObject.getInfo()))
                .flatMap(o -> caseService.updateCaseObjectMeta(authToken, new CaseObjectMeta(caseObject)))
                .flatMap(o -> caseService.updateCaseObjectMetaNotifiers(authToken, new CaseObjectMetaNotifiers(caseObject)))
                .flatMap(o -> {
                    if (En_ExtAppType.JIRA.getCode().equals(caseObject.getExtAppType())) {
                        return caseService.updateCaseObjectMetaJira(authToken, new CaseObjectMetaJira(caseObject));
                    }
                    return ok();
                })
                .map(ignore -> caseObject)
                .orElseGet(result -> error(result.getStatus(), "Service Error"));

        } catch (IllegalArgumentException  ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INCORRECT_PARAMS, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR, ex.getMessage());
        }
    }

    @PostMapping(value = "/products/{id:[0-9]+}")
    public Result<DevUnitInfo> getProductInfo( HttpServletRequest request, HttpServletResponse response,
                                               @PathVariable("id") Long productId ) {
        log.info( "getProductInfo() productId={}", productId);

        return authenticate( request, response, authService, sidGen, log ).flatMap( authToken ->
                productService.getProductInfo( authToken, productId ) )
                .ifOk( id -> log.info( "getProductInfo(): OK " ) )
                .ifError( result -> log.warn( "getProductInfo(): Can`t get info for product id={}. {}",
                        productId, result ) );
    }

    @PostMapping(value = "/products/create")
    public Result<DevUnitInfo> createProductByInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody DevUnitInfo product) {
        log.info("createProduct() product={} ", product);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> productService.createProductByInfo(authToken, product))
                .ifOk(devUnit -> log.info("createProduct(): OK"))
                .ifError(devUnitResult -> log.warn("createProduct(): Can't create product={}. {}", product, devUnitResult));
    }

    @PostMapping(value = "/products/update")
    public Result<Long> updateProductByInfo( HttpServletRequest request, HttpServletResponse response,
                                             @RequestBody DevUnitInfo product) {
        log.info( "updateProductByInfo() product={} ", product);

        return authenticate(request, response, authService, sidGen, log ).flatMap( authToken ->
                productService.updateProductFromInfo( authToken, product ) )
                .ifOk( id -> log.info( "updateProductByInfo(): OK " ) )
                .ifError( result -> log.warn( "updateProductByInfo(): Can`t update product by info={}. {}",
                        product, result ) );
    }

    @PostMapping(value = "/updateYoutrackCrmNumbers/{youtrackId}", produces = "text/plain;charset=UTF-8")
    public String updateYoutrackCrmNumbers( HttpServletRequest request, HttpServletResponse response,
                                                 @RequestBody (required = false) String crmNumbers,
                                                 @PathVariable("youtrackId") String youtrackId ) {

        final String OK = "";
        final String CRM_NUMBERS_NOT_FOUND = "Не найдены задачи с номерами: ";
        final String INTERNAL_ERROR = "Внутренняя ошибка на портале";
        final String INCORRECT_PARAMS = "Некорректно заданы номера обращений. Номера обращений должны содержать только цифры";

        log.info( "updateYoutrackCrmNumbers() crmNumbers={} youtrackId={}", crmNumbers, youtrackId );

        Result<List<Long>> makeNumberListResult = makeNumberList(crmNumbers)
                .flatMap(this::removeDuplicates);

        if (makeNumberListResult.isError()){
            log.error("updateYoutrackCrmNumbers(): failed to parse crm numbers, status={}", makeNumberListResult.getStatus());
            return INCORRECT_PARAMS;
        }

        Result<String> updateResult = authenticate(request, response, authService, sidGen, log)
                .flatMap( token -> caseLinkService.setYoutrackIdToCaseNumbers( token, youtrackId, makeNumberListResult.getData() ));

        if (updateResult.isOk()) {
            log.info( "updateYoutrackCrmNumbers(): OK" );
            return OK;
        }

        log.warn( "updateYoutrackCrmNumbers(): Can`t change youtrack id {} in cases with numbers {}. status: {}",
                youtrackId, crmNumbers, updateResult.getStatus() );

        if (En_ResultStatus.NOT_FOUND.equals(updateResult.getStatus())){
            return CRM_NUMBERS_NOT_FOUND + updateResult.getMessage();
        }

        return INTERNAL_ERROR;

    }

    @PostMapping(value = "/updateYoutrackProjectNumbers/{youtrackId}", produces = "text/plain;charset=UTF-8")
    public String updateYoutrackProjectNumbers( HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody (required = false) String projectNumbers,
                                            @PathVariable("youtrackId") String youtrackId ) {

        final String OK = "";
        final String PROJECT_NUMBERS_NOT_FOUND = "Не найдены проекты с номерами: ";
        final String INTERNAL_ERROR = "Внутренняя ошибка на портале";
        final String INCORRECT_PARAMS = "Некорректно заданы номера проектов. Номера проектов должны содержать только цифры";

        log.info( "updateYoutrackProjectNumbers() projectNumbers={} youtrackId={}", projectNumbers, youtrackId );

        Result<List<Long>> makeNumberListResult = makeNumberList(projectNumbers)
                .flatMap(this::removeDuplicates);

        if (makeNumberListResult.isError()){
            log.error("updateYoutrackCrmNumbers(): failed to parse crm numbers, status={}", makeNumberListResult.getStatus());
            return INCORRECT_PARAMS;
        }

        Result<String> updateResult = authenticate(request, response, authService, sidGen, log)
                .flatMap( token -> caseLinkService.setYoutrackIdToProjectNumbers( token, youtrackId, makeNumberListResult.getData() ));

        if (updateResult.isOk()) {
            log.info( "updateYoutrackProjectNumbers(): OK" );
            return OK;
        }

        log.warn( "updateYoutrackProjectNumbers(): Can`t change youtrack id {} in projects with numbers {}. status: {}",
                youtrackId, projectNumbers, updateResult.getStatus() );

        if (En_ResultStatus.NOT_FOUND.equals(updateResult.getStatus())){
            return PROJECT_NUMBERS_NOT_FOUND + updateResult.getMessage();
        }

        return INTERNAL_ERROR;

    }

    @PostMapping(value = "/deleteYoutrackCommentFromProjects/{youtrackId}", produces = "text/plain;charset=UTF-8")
    public String deleteYoutrackCommentFromProjects( HttpServletRequest request, HttpServletResponse response,
                                                     @PathVariable("youtrackId") String youtrackId,
                                                     @RequestBody String issueCommentJson ) {

        log.info( "deleteYoutrackCommentFromProjects() youtrackId={} issueCommentJson={}", youtrackId, issueCommentJson );

        final String OK = "";
        final String INTERNAL_ERROR = "Внутренняя ошибка на портале";
        final String COMMENT_NOT_CORRECT = "Формат комментария, полученного на портале, не корректен. Обратитесь в поддержку портала или Youtrack";

        YtIssueComment ytIssueComment = deserializeComment(issueCommentJson);

        if (ytIssueComment == null) {
            log.warn("saveYoutrackCommentToProjects(): ytIssueComment is null!");
            return COMMENT_NOT_CORRECT;
        }

        Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

        if (authTokenAPIResult.isError()) {
            log.warn( "deleteYoutrackCommentFromProjects(): authenticate failure" );
            return INTERNAL_ERROR;
        }

        AuthToken token = authTokenAPIResult.getData();
        Result<Boolean> deleteResult = caseCommentService.deleteProjectCommentsFromYoutrack(token, ytIssueComment.id);


        if (deleteResult.isOk()) {
            log.info( "deleteYoutrackCommentFromProjects(): OK" );
            return OK;
        }

        log.warn( "deleteYoutrackCommentFromProjects(): Can`t delete comment, status: {}", deleteResult.getStatus() );

        return INTERNAL_ERROR;
    }

    @PostMapping(value = "/saveYoutrackCommentToProjects/{youtrackId}", produces = "text/plain;charset=UTF-8")
    public String saveYoutrackCommentToProjects( HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable("youtrackId") String youtrackId,
                                                 @RequestBody String issueCommentJson ) {

        log.info( "saveYoutrackCommentToProjects() youtrackId={} issueCommentJson={}", youtrackId, issueCommentJson );

        final String OK = "";
        final String INTERNAL_ERROR = "Внутренняя ошибка на портале";
        final String PROJECT_NOT_UPDATED = "Ошибка добавления комментария для поектов: ";
        final String COMMENT_NOT_CORRECT = "Формат комментария, полученного на портале, не корректен. Обратитесь в поддержку портала или Youtrack";
        final String EMPTY_TEXT = "Текст комментария не должен быть пустым";

        YtIssueComment ytIssueComment = deserializeComment(issueCommentJson);

        if (ytIssueComment == null) {
            log.warn("saveYoutrackCommentToProjects(): ytIssueComment is null!");
            return COMMENT_NOT_CORRECT;
        }

        if (removeTag(ytIssueComment.text).trim().isEmpty()){
            log.warn("saveYoutrackCommentToProjects(): ytIssueComment text is empty");
            return EMPTY_TEXT;
        }

        Result<String> saveResult = authenticate(request, response, authService, sidGen, log)
                .flatMap(token ->  caseLinkService.getLinkedProjectIdsByYoutrackId(token, youtrackId)
                .flatMap(projectIds -> {
                    log.info( "saveYoutrackCommentToProjects(): linkedProjectIds={}", projectIds );

                    Result<List<CaseComment>> listResult = makeCommentsToCreate(token, projectIds, ytIssueComment, youtrackId);

                    if (listResult.isError()){
                        return error(listResult.getStatus());
                    }

                    List<CaseComment> commentToCreate = listResult.getData();
                    log.info( "saveYoutrackCommentToProjects(): commentToCreate={}", commentToCreate );

                    List<Long> errorResultProjectIds = new ArrayList<>();

                    Result<Boolean> caseCommentResultUpdate = caseCommentService.updateProjectCommentsFromYoutrack(token, convertYtIssueComment(ytIssueComment));
                    if (caseCommentResultUpdate.isError()){
                        log.warn( "saveYoutrackCommentToProjects(): update comment error, caseComment={}, remoteId={}", convertYtIssueComment(ytIssueComment), ytIssueComment.id );
                        return error(caseCommentResultUpdate.getStatus());
                    }

                    for (CaseComment caseComment : commentToCreate) {
                        Result<CaseComment> caseCommentResult = caseCommentService.addCaseComment(token, En_CaseType.PROJECT, caseComment);

                        if (caseCommentResult.isError()){
                            log.warn( "saveYoutrackCommentToProjects(): create comment error, projectId={}, caseComment={}", caseComment.getCaseId(), caseComment );
                            errorResultProjectIds.add(caseComment.getCaseId());
                        }
                    }

                    if (errorResultProjectIds.isEmpty()){
                        return ok();
                    } else {
                        return error(En_ResultStatus.NOT_UPDATED, errorResultProjectIds.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                    }
                }));

        if (saveResult.isOk()) {
            log.info( "saveYoutrackCommentToProjects(): OK" );
            return OK;
        }

        if (En_ResultStatus.NOT_UPDATED.equals(saveResult.getStatus())){
            return PROJECT_NOT_UPDATED + saveResult.getMessage();
        }

        log.warn( "saveYoutrackCommentToProjects(): Can`t save comment, status: {}", saveResult.getStatus() );

        return INTERNAL_ERROR;
    }

    private Result<List<CaseComment>> makeCommentsToCreate (AuthToken token, List<Long> projectIds, YtIssueComment ytIssueComment, String youtrackId){
        List<CaseComment> commentToCreate = new ArrayList<>();

        for (Long projectId : projectIds) {

            Result<List<CaseComment>> caseCommentList = caseCommentService.getCaseCommentList(token, En_CaseType.PROJECT, projectId);

            if (caseCommentList.isError()) {
                log.warn( "makeCommentsToCreate(): getCaseCommentList error, projectId={}, caseCommentList result={}", projectId, caseCommentList );
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            CaseComment existedCaseComment = findCaseCommentByRemoteId(caseCommentList.getData(), ytIssueComment.id);

            if(existedCaseComment == null) {
                CaseComment caseComment = convertYtIssueComment(ytIssueComment);
                caseComment.setCaseId(projectId);
                Result<CaseLink> ytLink = caseLinkService.getYtLink(token, youtrackId, projectId);

                if (ytLink.isError()) {
                    log.warn( "makeCommentsToCreate(): getYtLink error, projectId={}, ytLink result={}", projectId, ytLink );
                    return error(En_ResultStatus.INTERNAL_ERROR);
                }

                caseComment.setRemoteLinkId(ytLink.getData().getId());
                commentToCreate.add(caseComment);
            }
        }

        return ok(commentToCreate);
    }

    @PostMapping(value = "/changeyoutrackid/{oldyoutrackid}/{newyoutrackid}", produces = "text/plain;charset=UTF-8")
    public String changeYoutrackId( HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("oldyoutrackid") String oldYoutrackId,
                                    @PathVariable("newyoutrackid") String newYoutrackId ) {

        log.info( "changeYoutrackId() oldYoutrackId={} newYoutrackId={}", oldYoutrackId, newYoutrackId );

        final String OK = "";
        final String INTERNAL_ERROR = "Внутренняя ошибка на портале";

        Result<String> changeResult = authenticate(request, response, authService, sidGen, log)
                .flatMap( token -> caseLinkService.changeYoutrackId( token, oldYoutrackId, newYoutrackId ));

        if (changeResult.isOk()) {
            log.info( "changeYoutrackId(): OK" );
            return OK;
        }

        log.warn( "changeYoutrackId(): Can`t change youtrack id, status: {}", changeResult.getStatus() );

        return INTERNAL_ERROR;
    }

    @PostMapping(value = "/comments")
    public Result<List<CaseCommentShortView>> getCaseCommentList(
            @RequestBody CaseCommentApiQuery query,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("API | getCaseCommentList(): query={}", query);

        Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

        if (authTokenAPIResult.isError()) {
            return error(authTokenAPIResult.getStatus(), authTokenAPIResult.getMessage());
        }

        if (query.getCaseNumber() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS, "Required case number");
        }
        return caseCommentService.getCaseCommentShortViewList(authTokenAPIResult.getData(), En_CaseType.CRM_SUPPORT, makeCaseCommentQuery(query)).map( SearchResult::getResults );
    }

    @PostMapping(value = "/employees")
    public Result<List<PersonInfo>> getEmployees(HttpServletRequest request, HttpServletResponse response, @RequestBody EmployeeApiQuery query) {
        log.info("API | getEmployees(): query={}", query);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> employeeService.employeeList(authToken, makeEmployeeQuery(query)))
                .map(SearchResult::getResults)
                .map(employeeShortViews -> employeeShortViews.stream().map(PersonInfo::fromEmployeeShortView).collect(Collectors.toList()))
                .ifOk(personInfos -> log.info("getEmployees(): OK"))
                .ifError(result -> log.warn("getEmployees(): Can't find personInfos by query={}. {}", query, result));
    }

    @PostMapping(value = "/tags/create")
    public Result<CaseTag> createCaseTag(HttpServletRequest request, HttpServletResponse response, @RequestBody CaseTagInfo caseTagInfo) {
        log.info("API | createCaseTag(): caseTagInfo={}", caseTagInfo);

        Result<AuthToken> authenticate = authenticate(request, response, authService, sidGen, log);

        if (authenticate.isError()) {
            return error(authenticate.getStatus(), authenticate.getMessage());
        }

        AuthToken authToken = authenticate.getData();

        return caseTagService.create(authToken, CaseTagInfo.toCaseTag(caseTagInfo))
                .flatMap(tagId -> caseTagService.getTag(authToken, tagId))
                .ifOk(caseTagId -> log.info("createCaseTag(): OK"))
                .ifError(result -> log.warn("createCaseTag(): Can't create tag={}. {}", caseTagInfo, result));
    }

    @PostMapping(value = "/tags/remove/{caseTagId:[0-9]+}")
    public Result<Long> removeCaseTag(HttpServletRequest request, HttpServletResponse response, @PathVariable("caseTagId") Long caseTagId) {
        log.info("API | removeCaseTag(): caseTagId={}", caseTagId);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> caseTagService.removeTag(authToken, caseTagId))
                .ifOk(id -> log.info("removeCaseTag(): OK"))
                .ifError(result -> log.warn("removeCaseTag(): Can't remove tag={}. {}", caseTagId, result));
    }

    private CaseQuery makeCaseQuery(CaseApiQuery apiQuery) {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, apiQuery.getSearchString(), apiQuery.getSortField(), apiQuery.getSortDir());
        query.setLimit(apiQuery.getLimit());
        query.setOffset(apiQuery.getOffset());
        // optional
        query.setStateIds(apiQuery.getStateIds());
        query.setManagerIds(apiQuery.getManagerIds());
        query.setCompanyIds(apiQuery.getCompanyIds());
        query.setAllowViewPrivate(apiQuery.isAllowViewPrivate());
        query.setCreatedRange(new DateRange(En_DateIntervalType.FIXED,
                                            parseDate(apiQuery.getCreatedFrom()),
                                            parseDate(apiQuery.getCreatedTo()))
                             );
        query.setManagerCompanyIds(apiQuery.getManagerCompanyIds());
        return query;
    }

    private CaseCommentQuery makeCaseCommentQuery(CaseCommentApiQuery apiQuery) {
        CaseCommentQuery query = new CaseCommentQuery();
        query.setLimit(apiQuery.getLimit());
        query.setOffset(apiQuery.getOffset());
        query.setSortField(En_SortField.creation_date);
        query.setSortDir(En_SortDir.DESC);
        query.setCaseNumber(apiQuery.getCaseNumber());
        return query;
    }

    private EmployeeQuery makeEmployeeQuery(EmployeeApiQuery apiQuery) {
        EmployeeQuery query = new EmployeeQuery();
        query.setSearchString(apiQuery.getDisplayName());
        query.setEmail(apiQuery.getEmail());
        query.setWorkPhone(apiQuery.getWorkPhone());
        query.setMobilePhone(apiQuery.getMobilePhone());
        query.setLimit(apiQuery.getLimit());
        query.setOffset(apiQuery.getOffset());

        return query;
    }

    private Date parseDate(String date) {
        Date res;
        if (date == null)
            return null;
        if ((res = doParseDate(date, "yyyy-MM-dd HH:mm:ss.S")) != null)
            return res;
        if ((res = doParseDate(date, "yyyy-MM-dd HH:mm:ss")) != null)
            return res;
        if ((res = doParseDate(date, "yyyy-MM-dd")) != null)
            return res;
        return null;
    }

    private Date doParseDate(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (Throwable ex) {
            return null;
        }
    }

    private Result<List<Long>> removeDuplicates(List<Long> crmNumberList) {
        Set<Long> uniqueNumbers = new LinkedHashSet<>(crmNumberList);
        crmNumberList.clear();
        crmNumberList.addAll(uniqueNumbers);
        return ok(crmNumberList);
    }

    private Result<List<Long>> makeNumberList(String crmNumbers) throws NumberFormatException{
        try {
            if (crmNumbers == null) {
                return ok(new ArrayList<>());
            }
            crmNumbers = crmNumbers.replace("\n", "");

            return ok(Arrays.stream(crmNumbers.split(","))
                    .filter(Objects::nonNull)
                    .map(number -> {
                        if (number.startsWith("[")) {
                            return Long.parseLong(number.substring(1, number.indexOf("]")));
                        } else {
                            return Long.parseLong(number);
                        }
                    }).collect(Collectors.toList()));

        } catch (NumberFormatException e){
            log.error("makeNumberList(): failed to parse numbers", e);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
    }

    private CaseComment convertYtIssueComment(YtIssueComment issueComment) {
        CaseComment caseComment = new CaseComment();
        caseComment.setAuthorId(config.data().youtrack().getYoutrackUserId());
        caseComment.setCreated(issueComment.created == null ? null : new Date(issueComment.created));
        caseComment.setUpdated(issueComment.updated == null ? null : new Date(issueComment.updated));
        caseComment.setRemoteId(issueComment.id);
        caseComment.setOriginalAuthorName(issueComment.author != null ? issueComment.author.fullName : null);
        caseComment.setOriginalAuthorFullName(issueComment.author != null ? issueComment.author.fullName : null);
        caseComment.setText(removeTag(issueComment.text));
        caseComment.setDeleted(issueComment.deleted == null ? false : issueComment.deleted);
        //Заглушка
        caseComment.setCaseAttachments(new ArrayList<>());
        return caseComment;
    }

    private String removeTag (String text){
        final Pattern TAG_PATTERN = Pattern.compile("^\\s*@crm\\s*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = TAG_PATTERN.matcher(text);
        if (matcher.find()){
            return text.substring(matcher.end());
        }

        return text;
    }

    private CaseComment findCaseCommentByRemoteId(List<CaseComment> list, String remoteId) {
        for (CaseComment caseComment : list) {
            if (Objects.equals(caseComment.getRemoteId(), remoteId)){
                return caseComment;
            }
        }
        return null;
    }

    private YtIssueComment deserializeComment (String json){
        ObjectMapper mapper = YtDtoObjectMapperProvider.getMapper(fieldsMapper);
        YtIssueComment ytIssueComment;
        try {
            ytIssueComment = mapper.readValue(json, YtIssueComment.class);
            return ytIssueComment;
        } catch (IOException e) {
            log.error("deserializeComment(): failed to deserialize ytIssueComment", e);
            return null;
        }
    }
}