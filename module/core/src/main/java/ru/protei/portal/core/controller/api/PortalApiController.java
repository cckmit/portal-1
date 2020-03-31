package ru.protei.portal.core.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.CaseTagInfo;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.dto.PersonInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
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
    CaseCommentService caseCommentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CaseTagService caseTagService;


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


    @PostMapping(value = "/addyoutrackidintoissue/{youtrackId}/{caseNumber:[0-9]+}")
    public Result<Long> addYoutrackIdIntoIssue( HttpServletRequest request, HttpServletResponse response,
                                                @PathVariable("caseNumber") Long caseNumber,
                                                @PathVariable("youtrackId") String youtrackId ) {
        log.info( "addYoutrackIdIntoIssue() caseNumber={} youtrackId={}", caseNumber, youtrackId );

        return authenticate(request, response, authService, sidGen, log).flatMap( token ->
                caseLinkService.addYoutrackLink( token, caseNumber, youtrackId ) )
                .ifOk( id -> log.info( "addYoutrackIdIntoIssue(): OK " ) )
                .ifError( result -> log.warn( "addYoutrackIdIntoIssue(): Can`t add youtrack id {} into case with number {}. status: {}",
                        youtrackId, caseNumber, result ) );

    }

    @PostMapping(value = "/removeyoutrackidfromissue/{youtrackId}/{caseNumber:[0-9]+}")
    public Result<Long> removeYoutrackIdIntoIssue( HttpServletRequest request, HttpServletResponse response,
                                                      @PathVariable("caseNumber") Long caseNumber,
                                                      @PathVariable("youtrackId") String youtrackId ) {
        log.info( "removeYoutrackIdIntoIssue() caseNumber={} youtrackId={}", caseNumber, youtrackId );

        return authenticate(request, response, authService, sidGen, log).flatMap( token ->
                caseLinkService.removeYoutrackLink( token, caseNumber, youtrackId ) )
                .ifOk( isSucces -> log.info( "removeYoutrackIdIntoIssue(): OK" ) )
                .ifError( result -> log.warn( "removeYoutrackIdIntoIssue(): Can`t remove youtrack id {} from case with number {}. status: {}",
                        youtrackId, caseNumber, result ) );
    }

    @PostMapping(value = "/changeyoutrackidinissue/{youtrackId}/{oldCaseNumber:[0-9]+}/{newCaseNumber:[0-9]+}")
    public Result<Long> changeYoutrackIdInIssue( HttpServletRequest request, HttpServletResponse response,
                                                 @PathVariable("oldCaseNumber") Long oldCaseNumber,
                                                 @PathVariable("newCaseNumber") Long newCaseNumber,
                                                 @PathVariable("youtrackId") String youtrackId ) {
        log.info( "changeYoutrackIdInIssue() oldCaseNumber={} newCaseNumber={} youtrackId={}", oldCaseNumber, newCaseNumber, youtrackId );

        // Нужно отвязать youtrack задачу от старого обращения и затем привязать к новому обращению
        return authenticate(request, response, authService, sidGen, log).flatMap( token ->
                caseLinkService.removeYoutrackLink( token, oldCaseNumber, youtrackId ).flatMap( aBoolean -> ok( token ) ) ).flatMap( token ->
                caseLinkService.addYoutrackLink( token, newCaseNumber, youtrackId ) )
                .ifOk( linkId -> log.info( "changeYoutrackIdInIssue(): OK" ) )
                .ifError( result -> log.warn( "changeYoutrackIdInIssue(): Can`t change youtrack id {} in case with number {}. status: {}",
                        youtrackId, oldCaseNumber, result ) );
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
        query.setStateIds(getCaseStateIdList(apiQuery.getStates()));
        query.setManagerIds(apiQuery.getManagerIds());
        query.setCompanyIds(apiQuery.getCompanyIds());
        query.setAllowViewPrivate(apiQuery.isAllowViewPrivate());
        query.setCreatedFrom(parseDate(apiQuery.getCreatedFrom()));
        query.setCreatedTo(parseDate(apiQuery.getCreatedTo()));
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

    private List<Integer> getCaseStateIdList(List<String> states) {
        List<Integer> stateIds = null;
        if (CollectionUtils.isNotEmpty(states)) {
            stateIds = Arrays.asList(En_CaseState.values()).stream()
                    .filter(state -> states.contains(state.name()))
                    .map(En_CaseState::getId)
                    .collect(Collectors.toList());
        }
        return stateIds;
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
}