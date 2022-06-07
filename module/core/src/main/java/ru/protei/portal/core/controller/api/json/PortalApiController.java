package ru.protei.portal.core.controller.api.json;

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
import ru.protei.portal.core.model.api.ApiAbsence;
import ru.protei.portal.core.model.api.ApiContract;
import ru.protei.portal.core.model.api.ApiDocument;
import ru.protei.portal.core.model.api.ApiProject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.CaseTagInfo;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.dto.PersonInfo;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.mapper.ContractToApiMapper;
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
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.util.CrmConstants.Header.X_REAL_IP;
import static ru.protei.portal.mapper.ApiProjectToProjectMapper.toProject;
import static ru.protei.portal.util.AuthUtils.authenticate;

/**
 * Севрис для  API
 */
@RestController
@RequestMapping(value = "/api", headers = "Accept=application/json",
        produces = "application/json", consumes = "application/json")
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
    private YoutrackService youtrackService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private SiteFolderService siteFolderService;
    @Autowired
    private AbsenceService absenceService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private CaseElapsedTimeApiService caseElapsedTimeApiService;
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
    public Result<CaseObject> createCase(@RequestBody CaseObject caseObject,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        log.info("API | createCase(): caseObject={}", caseObject);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> caseService.createCaseObject(authToken, new CaseObjectCreateRequest(caseObject)))
                .ifError(result -> log.warn("createCase(): Can't create caseObject={}. {}", caseObject, result))
                .ifOk(object -> log.info("createCase(): OK"));

    }

    @PostMapping(value = "/cases/update")
    public Result<CaseObject> updateCase(@RequestBody CaseObject caseObject,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        log.info("API | updateCase(): caseObject={}", caseObject);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken ->
                        caseService.updateCaseNameAndDescription(authToken, new CaseNameAndDescriptionChangeRequest(caseObject.getId(), caseObject.getName(), caseObject.getInfo()), En_CaseType.CRM_SUPPORT)
                        .flatMap(o -> caseService.updateCaseObjectMeta(authToken, new CaseObjectMeta(caseObject)))
                        .flatMap(o -> caseService.updateCaseObjectMetaNotifiers(authToken, En_CaseType.CRM_SUPPORT, new CaseObjectMetaNotifiers(caseObject)))
                        .flatMap(o -> {
                            if (En_ExtAppType.JIRA.getCode().equals(caseObject.getExtAppType())) {
                                return caseService.updateCaseObjectMetaJira(authToken, new CaseObjectMetaJira(caseObject));
                            }
                            return ok();
                        }))
                .map(ignore -> caseObject)
                .ifError(result -> log.warn("updateCase(): Can't update caseObject={}. {}", caseObject, result))
                .ifOk(object -> log.info("updateCase(): OK"));
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

    @PostMapping(value = "/getProductShortViews")
    public Result<List<ProductShortView>> getProductShortViews(HttpServletRequest request, HttpServletResponse response,
                                                               @RequestBody ProductQuery query ) {
        log.info( "getProductShortViews() query={}", query);

        return authenticate( request, response, authService, sidGen, log ).flatMap( authToken ->
                        productService.shortViewList( authToken, query ) )
                .ifOk( id -> log.info( "getProductShortViews(): OK " ) )
                .ifError( result -> log.warn( "getProductShortViews(): Can`t get info for query id={}. {}",
                        query, result ) );
    }

    @PostMapping(value = "/products/create")
    public Result<DevUnitInfo> createProductByInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody DevUnitInfo product) {
        log.info("createProduct() product={} ", product);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> productService.createProductByInfo(authToken, product))
                .ifError(devUnitResult -> log.warn("createProduct(): Can't create product={}. {}", product, devUnitResult))
                .ifOk(devUnit -> log.info("createProduct(): OK"));
    }

    @PostMapping(value = "/products/updateState/{productId}/{productState}")
    public Result<?> updateProductState(HttpServletRequest request, HttpServletResponse response,
                                        @PathVariable("productId") Long productId,
                                        @PathVariable("productState") Integer productState) {

        log.info("API | updateProductState(): productId={}, productState={}", productId, productState);

        final String INCORRECT_STATE = "Некорректный статус продукта. Допустимые значения: 1/2";

        Result<AuthToken> authenticate = authenticate(request, response, authService, sidGen, log);

        if (authenticate.isError()) {
            return error(authenticate.getStatus(), authenticate.getMessage());
        }

        AuthToken authToken = authenticate.getData();

        En_DevUnitState state = En_DevUnitState.forId(productState);
        if (state == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS, INCORRECT_STATE);
        }

        return productService.updateState(authToken, productId, state)
                .ifOk(result -> log.info("updateProductState(): OK"))
                .ifError(result -> log.warn("updateProductState(): Can't update product state with id={}. {}", productId, result));
    }

    /**
     * Получение списка продуктов и субпродуктов (в иерархии) для проектов компании-заказчика
     * @return список DevUnitInfo
     */
    @GetMapping(value = "/products/getByCompanyProjects")
    public Result<List<DevUnitInfo>> getProductsByCompanyProjects( HttpServletRequest request, HttpServletResponse response) {
        log.info( "getProductsByCompanyProjects" );

        return authenticate( request, response, authService, sidGen, log ).flatMap( authToken ->
                productService.getProductsBySelfCompanyProjects( authToken ) )
                .ifOk( id -> log.info( "getProductsByCompanyProjects(): OK " ) )
                .ifError( result -> log.warn( "getProductsByCompanyProjects(): Can`t get products. {}", result ) );
    }

    @GetMapping(value = "/authorization")
    public Result<AuthToken> authorization(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam("login") String login,
                                   @RequestParam("password") String password) {
        log.info( "authorization() login = {}", login );

        return authService.login(sidGen.generateId(), login, password, request.getHeader(X_REAL_IP),
                request.getHeader(CrmConstants.Header.USER_AGENT) )
                .ifOk( id -> log.info( "authorization(): OK " ) )
                .ifError( result -> log.warn( "authorization(): Can`t authenticate. {}", result ) );
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

        Result<String> updateResult = authenticate(request, response, authService, sidGen, log)
                .flatMap( token -> updateCrmNumbers(token, crmNumbers, youtrackId));

        if (updateResult.isError()) {
            log.warn( "updateYoutrackCrmNumbers(): Can`t change youtrack id {} in cases with numbers {}. status: {}",
                    youtrackId, crmNumbers, updateResult.getStatus() );

            if (En_ResultStatus.INCORRECT_PARAMS.equals(updateResult.getStatus())) {
                log.error("updateYoutrackCrmNumbers(): failed to parse crm numbers, updateResult={}", updateResult);
                return INCORRECT_PARAMS;
            }

            if (En_ResultStatus.NOT_FOUND.equals(updateResult.getStatus())){
                return CRM_NUMBERS_NOT_FOUND + updateResult.getMessage();
            }

            return INTERNAL_ERROR;
        }

        log.info( "updateYoutrackCrmNumbers(): OK" );
        return OK;
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

        Result<String> updateResult = authenticate(request, response, authService, sidGen, log)
                .flatMap( token -> updateProjectNumbers( token, projectNumbers, youtrackId));

        if (updateResult.isError()) {
            log.warn( "updateYoutrackProjectNumbers(): Can`t change youtrack id {} in projects with numbers {}. status: {}",
                    youtrackId, projectNumbers, updateResult.getStatus() );

            if (En_ResultStatus.NOT_FOUND.equals(updateResult.getStatus())){
                return PROJECT_NUMBERS_NOT_FOUND + updateResult.getMessage();
            }

            if (En_ResultStatus.INCORRECT_PARAMS.equals(updateResult.getStatus())) {
                log.error("updateYoutrackCrmNumbers(): failed to parse crm numbers, updateResult={}", updateResult);
                return INCORRECT_PARAMS;
            }

            return INTERNAL_ERROR;
        }

        log.info( "updateYoutrackProjectNumbers(): OK" );
        return OK;
    }

    @PostMapping(value = "/deleteYoutrackCommentFromProjects/{youtrackId}", produces = "text/plain;charset=UTF-8")
    public String deleteYoutrackCommentFromProjects( HttpServletRequest request, HttpServletResponse response,
                                                     @PathVariable("youtrackId") String youtrackId,
                                                     @RequestBody String issueCommentJson ) {

        log.info( "deleteYoutrackCommentFromProjects() youtrackId={} issueCommentJson={}", youtrackId, issueCommentJson );

        final String OK = "";
        final String INTERNAL_ERROR = "Внутренняя ошибка на портале";
        final String COMMENT_NOT_CORRECT = "Формат комментария, полученного на портале, не корректен. Обратитесь в поддержку портала или Youtrack";

        Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

        if (authTokenAPIResult.isError()) {
            log.warn( "deleteYoutrackCommentFromProjects(): authenticate failure" );
            return INTERNAL_ERROR;
        }

        YtIssueComment ytIssueComment = deserializeComment(issueCommentJson);

        if (ytIssueComment == null) {
            log.warn("saveYoutrackCommentToProjects(): ytIssueComment is null!");
            return COMMENT_NOT_CORRECT;
        }

        AuthToken token = authTokenAPIResult.getData();
        Result<Boolean> deleteResult = caseCommentService.deleteProjectCommentsFromYoutrack(token, ytIssueComment.id);


        if (deleteResult.isError()) {
            log.warn( "deleteYoutrackCommentFromProjects(): Can`t delete comment, status: {}", deleteResult.getStatus() );
            return INTERNAL_ERROR;
        }

        log.info( "deleteYoutrackCommentFromProjects(): OK" );
        return OK;
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

        Result<Boolean> saveResult = authenticate(request, response, authService, sidGen, log)
                .flatMap(token ->  caseLinkService.getProjectIdsByYoutrackId(token, youtrackId)
                .flatMap(projectIds -> {
                    log.info( "saveYoutrackCommentToProjects(): projectIds={}", projectIds );

                    YtIssueComment ytIssueComment = deserializeComment(issueCommentJson);

                    if (ytIssueComment == null) {
                        log.warn("saveYoutrackCommentToProjects(): ytIssueComment is null!");
                        return error(En_ResultStatus.INCORRECT_PARAMS, COMMENT_NOT_CORRECT);
                    }

                    if (removeTag(ytIssueComment.text).trim().isEmpty()){
                        log.warn("saveYoutrackCommentToProjects(): ytIssueComment text is empty");
                        return error(En_ResultStatus.INCORRECT_PARAMS, EMPTY_TEXT);
                    }

                    return updateComments(token, ytIssueComment)
                            .flatMap(ignore -> makeCommentListToCreate(token, projectIds, ytIssueComment, youtrackId))
                            .flatMap(commentListToCreate -> createComments(token, commentListToCreate));
                }));

        if (saveResult.isError()) {
            log.warn( "saveYoutrackCommentToProjects(): Can`t save comment, status: {}", saveResult.getStatus() );

            if (En_ResultStatus.INCORRECT_PARAMS.equals(saveResult.getStatus())){
                return StringUtils.isEmpty(saveResult.getMessage()) ? INTERNAL_ERROR : saveResult.getMessage();
            }

            if (En_ResultStatus.NOT_UPDATED.equals(saveResult.getStatus())){
                return PROJECT_NOT_UPDATED + saveResult.getMessage();
            }

            return INTERNAL_ERROR;
        }

        log.info( "saveYoutrackCommentToProjects(): OK" );
        return OK;
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

    @PostMapping(value = "/case/histories")
    public Result<List<History>> getCaseHistory(
            @RequestBody HistoryApiQuery query,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("API | getCaseHistory(): query={}", query);

        Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

        if (authTokenAPIResult.isError()) {
            return error(authTokenAPIResult.getStatus(), authTokenAPIResult.getMessage());
        }

        if (query.getCaseNumber() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS, "Required case number");
        }
        return historyService.getCaseHistoryList(authTokenAPIResult.getData(), En_CaseType.CRM_SUPPORT, makeHistoryQuery(query) );
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

    @PostMapping(value = "/contracts/1c/get")
    public Result<List<ApiContract>> getContracts1cGet(HttpServletRequest request, HttpServletResponse response, @RequestBody ContractApiQuery contractApiQuery) {
        log.info("API | getContracts1cGet(): contractApiQuery={}", contractApiQuery);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> contractService.getContractsByApiQuery(authToken, contractApiQuery))
                .map(contracts -> stream(contracts)
                        .map(ContractToApiMapper::contractToApi)
                        .collect(Collectors.toList()))
                .ifOk(id -> log.info("getContracts1cGet(): OK"))
                .ifError(result -> log.warn("getContracts1cGet(): Can't get contracts by contractApiQuery={}. {}", contractApiQuery, result));
    }

    @PostMapping(value = "/companies/create")
    public Result<Company> createCompany(HttpServletRequest request, HttpServletResponse response, @RequestBody Company company) {
        log.info("API | createCompany(): company={}", company);

        Result<AuthToken> authenticate = authenticate(request, response, authService, sidGen, log);

        if (authenticate.isError()) {
            return error(authenticate.getStatus(), authenticate.getMessage());
        }

        AuthToken authToken = authenticate.getData();

        return companyService.createCompany(authToken, company)
                .ifOk(result -> log.info("createCompany(): OK"))
                .ifError(result -> log.warn("createCompany(): Can't create company={}. {}", company, result));
    }

    @PostMapping(value = "/companies/updateState/{companyId}/{isArchived}")
    public Result<?> updateCompanyState(HttpServletRequest request, HttpServletResponse response,
                                        @PathVariable("companyId") Long companyId,
                                        @PathVariable("isArchived") Boolean isArchived) {

        log.info("API | updateCompanyState(): companyId={}, isArchived={}" , companyId, isArchived);

        Result<AuthToken> authenticate = authenticate(request, response, authService, sidGen, log);

        if (authenticate.isError()) {
            return error(authenticate.getStatus(), authenticate.getMessage());
        }

        AuthToken authToken = authenticate.getData();

        return companyService.updateState(authToken, companyId, isArchived)
                .ifOk(result -> log.info("updateCompanyState(): OK"))
                .ifError(result -> log.warn("updateCompanyState(): Can't update company state with id={}. {}", companyId, result));
    }

    @PostMapping(value = "/platforms/create")
    public Result<Platform> createPlatform(HttpServletRequest request, HttpServletResponse response, @RequestBody Platform platform) {
        log.info("API | createPlatform(): platform={}", platform);

        Result<AuthToken> authenticate = authenticate(request, response, authService, sidGen, log);

        if (authenticate.isError()) {
            return error(authenticate.getStatus(), authenticate.getMessage());
        }

        AuthToken authToken = authenticate.getData();

        return siteFolderService.createPlatform(authToken, platform)
                .ifOk(result -> log.info("createPlatform(): OK"))
                .ifError(result -> log.warn("createPlatform(): Can't create platform={}. {}", platform, result));
    }

    @PostMapping(value = "/platforms/delete/{platformId}")
    public Result<?> deletePlatform(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("platformId") Long platformId) {

        log.info("API | deletePlatform(): platformId={}", platformId);

        Result<AuthToken> authenticate = authenticate(request, response, authService, sidGen, log);

        if (authenticate.isError()) {
            return error(authenticate.getStatus(), authenticate.getMessage());
        }

        AuthToken authToken = authenticate.getData();

        return siteFolderService.removePlatform(authToken, platformId)
                .ifOk(result -> log.info("deletePlatform(): OK"))
                .ifError(result -> log.warn("deletePlatform(): Can't delete platform with id={}. {}", platformId, result));
    }

    @PostMapping(value = "/doc/create")
    public Result<Document> createDocument(HttpServletRequest request, HttpServletResponse response,
                                          @RequestBody ApiDocument apiDocument) {
        log.info("API | createDocument(): apiDocument={}", apiDocument);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(token -> documentService.createDocumentByApi(token, apiDocument))
                .ifOk(caseTagId -> log.info("createDocument(): OK"))
                .ifError(result -> log.warn("createDocument(): Can't create document={}. {}", apiDocument, result));
    }

    @PostMapping(value = "/doc/remove/{documentId:[0-9]+}")
    public Result<Long> removeDocument(HttpServletRequest request, HttpServletResponse response, @PathVariable("documentId") Long documentId) {
        log.info("API | removeDocument(): id={}", documentId);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> documentService.removeDocumentByApi(authToken, documentId))
                .ifOk(id -> log.info("removeDocument(): OK"))
                .ifError(result -> log.warn("removeDocument(): Can't remove document={}. {}", documentId, result));
    }

    @PostMapping(value = "/absence/1c/get")
    public Result<List<ApiAbsence>> getAbsence1cGet(HttpServletRequest request, HttpServletResponse response, @RequestBody AbsenceApiQuery apiQuery) {
        log.info("API | getAbsence1cGet(): apiQuery={}", apiQuery);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> absenceService.getAbsencesByApiQuery(authToken, apiQuery))
                .ifOk(id -> log.info("getAbsence1cGet(): OK"))
                .ifError(result -> log.warn("getAbsence1cGet(): Can't get absences by apiQuery={}. {}", apiQuery, result));
    }

    @PostMapping(value = "/absence/1c/create")
    public Result<Long> createAbsence1c(HttpServletRequest request, HttpServletResponse response, @RequestBody ApiAbsence apiAbsence) {
        log.info("API | createAbsence1c(): apiAbsence={}", apiAbsence);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> absenceService.createAbsenceByApi(authToken, apiAbsence))
                .ifOk(id -> log.info("createAbsence1c(): OK"))
                .ifError(result -> log.warn("createAbsence1c(): Can't create absences by apiAbsence={}. {}", apiAbsence, result));
    }

    @PostMapping(value = "/absence/1c/update")
    public Result<Long> updateAbsence1c(HttpServletRequest request, HttpServletResponse response, @RequestBody ApiAbsence apiAbsence) {
        log.info("API | updateAbsence1c(): apiAbsence={}", apiAbsence);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> absenceService.updateAbsenceByApi(authToken, apiAbsence))
                .ifOk(id -> log.info("updateAbsence1c(): OK"))
                .ifError(result -> log.warn("updateAbsence1c(): Can't update absences by apiAbsence={}. {}", apiAbsence, result));
    }


    @PostMapping(value = "/absence/1c/remove")
    public Result<Long> removeAbsence1c(HttpServletRequest request, HttpServletResponse response, @RequestBody ApiAbsence apiAbsence) {
        log.info("API | removeAbsence1c(): apiAbsence={}", apiAbsence);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> absenceService.removeAbsenceByApi(authToken, apiAbsence))
                .ifOk(id -> log.info("removeAbsence1c(): OK"))
                .ifError(result -> log.warn("removeAbsence1c(): Can't remove absences by apiAbsence={}. {}", apiAbsence, result));
    }

    @PostMapping(value = "/projects/delete/{projectId}")
    public Result<Long> deleteProject(HttpServletRequest request, HttpServletResponse response,
                                      @PathVariable("projectId") Long projectId) {

        log.info("API | deleteProject(): projectId={}", projectId);

        Result<AuthToken> authenticate = authenticate(request, response, authService, sidGen, log);

        if (authenticate.isError()) {
            return error(authenticate.getStatus(), authenticate.getMessage());
        }

        AuthToken authToken = authenticate.getData();

        return projectService.removeProject(authToken, projectId)
                .ifOk(result -> log.info("deleteProject(): OK"))
                .ifError(result -> log.warn("deleteProject(): Can't delete project with id={}. {}", projectId, result));
    }

    @PostMapping(value = "/projects/create")
    public Result<Project> createProjectByApi(HttpServletRequest request, HttpServletResponse response, @RequestBody ApiProject apiProject) {
        log.info("API | createProjectByApi(): project={}", apiProject);

        return authenticate(request, response, authService, sidGen, log)
                .flatMap(authToken -> apiProject.isValid() ? ok(authToken) : error(En_ResultStatus.INCORRECT_PARAMS))
                .flatMap(authToken -> projectService.createProject(authToken, toProject(apiProject)))
                .ifError(project -> log.warn("createProjectByApi(): Can't create project={}. {}", project, project))
                .ifOk(result -> log.info("createProjectByApi(): OK"));
    }

    @PostMapping(value = "/case/elapsedTimes")
    public Result<List<CaseElapsedTimeApi>> getCaseElapsedTimes(
            @RequestBody CaseElapsedTimeApiQuery query,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("API | getCaseElapsedTime(): query={}", query);

        try {
            Result<AuthToken> authTokenAPIResult = authenticate(request, response, authService, sidGen, log);

            if (authTokenAPIResult.isError()) {
                return error(authTokenAPIResult.getStatus(), authTokenAPIResult.getMessage());
            }

            AuthToken authToken = authTokenAPIResult.getData();

            return caseElapsedTimeApiService.getByQuery(authToken, query);

        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INCORRECT_PARAMS, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return error(En_ResultStatus.INTERNAL_ERROR, ex.getMessage());
        }
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
        query.setViewPrivate(apiQuery.getViewPrivate());
        query.setCreatedRange(new DateRange(En_DateIntervalType.FIXED,
                                            parseDate(apiQuery.getCreatedFrom()),
                                            parseDate(apiQuery.getCreatedTo()))
                             );
        query.setModifiedRange(new DateRange(En_DateIntervalType.FIXED,
                apiQuery.getModifiedFrom(),
                apiQuery.getModifiedTo())
        );
        query.setManagerCompanyIds(apiQuery.getManagerCompanyIds());
        query.setProductIds(apiQuery.getProductIds());
        query.setCaseTagsNames(apiQuery.getCaseTagsNames());
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

    private HistoryQuery makeHistoryQuery(HistoryApiQuery apiQuery) {
        HistoryQuery query = new HistoryQuery();
        query.setLimit(apiQuery.getLimit());
        query.setOffset(apiQuery.getOffset());
        query.setSortField(En_SortField.id);
        query.setSortDir(En_SortDir.DESC);
        query.setCaseNumber(apiQuery.getCaseNumber());
        return query;
    }

    private EmployeeQuery makeEmployeeQuery(EmployeeApiQuery apiQuery) {
        EmployeeQuery query = new EmployeeQuery();
        query.setIds(apiQuery.getIds());
        query.setSearchString(apiQuery.getDisplayName());
        query.setEmailByLike(apiQuery.getEmail());
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
        return ok(crmNumberList
                .stream()
                .distinct()
                .collect(Collectors.toList()));
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

    private String removeTag (String text){
        final Pattern TAG_PATTERN = Pattern.compile("^\\s*@crm\\s*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = TAG_PATTERN.matcher(text);
        if (matcher.find()){
            return text.substring(matcher.end());
        }

        return text;
    }

    private Result<Boolean> checkCommentExisted(List<CaseComment> list, String remoteId) {
        for (CaseComment caseComment : list) {
            if (Objects.equals(caseComment.getRemoteId(), remoteId)){
                return ok(true);
            }
        }
        return ok(false);
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

    private Result<String> updateCrmNumbers(AuthToken token, String crmNumbers, String youtrackId) {
        return makeNumberList(crmNumbers)
                .flatMap(this::removeDuplicates)
                .flatMap(crmNumberListDistinct -> caseLinkService.setYoutrackIdToCaseNumbers(token, youtrackId, crmNumberListDistinct));
    }

    private Result<String> updateProjectNumbers(AuthToken token, String projectNumbers, String youtrackId) {
        return makeNumberList(projectNumbers)
                .flatMap(this::removeDuplicates)
                .flatMap(projectNumberListDistinct -> caseLinkService.setYoutrackIdToProjectNumbers(token, youtrackId, projectNumberListDistinct));
    }

    private Result<Boolean> updateComments(AuthToken token, YtIssueComment ytIssueComment){

        return youtrackService.convertYtIssueComment(ytIssueComment)
                .flatMap(caseComment -> {
                    caseComment.setText(removeTag(caseComment.getText()));
                    return caseCommentService.updateProjectCommentsFromYoutrack(token, caseComment);
                })
                .ifError(updateResult -> log.warn( "saveYoutrackCommentToProjects(): update comment error, caseComment={}, remoteId={}, result={}", youtrackService.convertYtIssueComment(ytIssueComment), ytIssueComment.id, updateResult));
    }

    private Result<Boolean> createComments(AuthToken token, List<CaseComment> commentListToCreate){

        log.info( "saveYoutrackCommentToProjects(): commentListToCreate={}", commentListToCreate );
        List<Long> errorResultProjectIds = new ArrayList<>();

        for (CaseComment caseComment : commentListToCreate) {
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
    }

    private Result<List<CaseComment>> makeCommentListToCreate(AuthToken token, List<Long> projectIds, YtIssueComment ytIssueComment, String youtrackId){
        log.info( "makeCommentListToCreate(): projectIds={}, ytIssueComment={}, youtrackId={}", projectIds, ytIssueComment, youtrackId );

        List<CaseComment> commentListToCreate = new ArrayList<>();

        for (Long projectId : projectIds) {

            Result<CaseComment> caseCommentResult = caseCommentService.getCaseCommentList(token, En_CaseType.PROJECT, projectId)
                    .ifError(getCommentResult ->  log.warn( "makeCommentListToCreate(): getCaseCommentList error, projectId={}, caseCommentList result={}", projectId, getCommentResult ))
                    .flatMap(caseComments -> checkCommentExisted(caseComments, ytIssueComment.id))
                    .flatMap(isExisted -> isExisted ? ok() : makeCommentToCreate(token, ytIssueComment, projectId, youtrackId));

            if (caseCommentResult.isError()) {
                log.warn( "makeCommentListToCreate(): failed to make comment to create, projectId={}, caseCommentResult={}", projectId, caseCommentResult );
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            if (caseCommentResult.getData() != null){
                commentListToCreate.add(caseCommentResult.getData());
            }
        }

        log.info( "makeCommentListToCreate(): commentListToCreate={}", commentListToCreate );
        return ok(commentListToCreate);
    }

    public Result<CaseComment> makeCommentToCreate(AuthToken token, YtIssueComment ytIssueComment, Long projectId, String youtrackId){

        Result<CaseComment> caseCommentResult = youtrackService.convertYtIssueComment(ytIssueComment);

        if (caseCommentResult.isError() || caseCommentResult.getData() == null){
            log.warn( "makeCommentsToCreate(): failed to convert comment. ytIssueComment={}, caseCommentResult={}", ytIssueComment, caseCommentResult );
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        CaseComment caseComment = caseCommentResult.getData();
        caseComment.setText(removeTag(caseComment.getText()));

        caseComment.setCaseId(projectId);
        Result<CaseLink> ytLink = caseLinkService.getYtLink(token, youtrackId, projectId);

        if (ytLink.isError()) {
            log.warn( "makeCommentsToCreate(): getYtLink error, projectId={}, ytLink result={}", projectId, ytLink );
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        caseComment.setRemoteLinkId(ytLink.getData().getId());

        return  ok(caseComment);
    }
}