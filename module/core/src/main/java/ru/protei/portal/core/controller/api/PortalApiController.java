package ru.protei.portal.core.controller.api;

import jdk.nashorn.internal.runtime.options.OptionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseLinkService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.util.AuthUtils;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

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
            Result<UserSessionDescriptor> userSessionDescriptorAPIResult = AuthUtils.authenticate(request, response, authService, sidGen, log);

            if (userSessionDescriptorAPIResult.isError()) {
                return error(userSessionDescriptorAPIResult.getStatus(), userSessionDescriptorAPIResult.getMessage());
            }

            AuthToken authToken = userSessionDescriptorAPIResult.getData().makeAuthToken();

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
            Result<UserSessionDescriptor> userSessionDescriptorAPIResult = AuthUtils.authenticate(request, response, authService, sidGen, log);

            if (userSessionDescriptorAPIResult.isError()) {
                return error(userSessionDescriptorAPIResult.getStatus(), userSessionDescriptorAPIResult.getMessage());
            }

            AuthToken authToken = userSessionDescriptorAPIResult.getData().makeAuthToken();

            Result<CaseObject> caseObjectCoreResponse = caseService.createCaseObject(
                    authToken,
                    (CaseObject) auditableObject,
                    userSessionDescriptorAPIResult.getData().getPerson()
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
            Result<UserSessionDescriptor> userSessionDescriptorAPIResult = AuthUtils.authenticate(request, response, authService, sidGen, log);

            if (userSessionDescriptorAPIResult.isError()) {
                return error(userSessionDescriptorAPIResult.getStatus(), userSessionDescriptorAPIResult.getMessage());
            }

            AuthToken authToken = userSessionDescriptorAPIResult.getData().makeAuthToken();

            Person person = userSessionDescriptorAPIResult.getData().getPerson();
            CaseObject caseObject = (CaseObject) auditableObject;

            return caseService.updateCaseObject(authToken, caseObject, person)
                .flatMap(o -> caseService.updateCaseObjectMeta(authToken, new CaseObjectMeta(caseObject), person))
                .flatMap(o -> caseService.updateCaseObjectMetaNotifiers(authToken, new CaseObjectMetaNotifiers(caseObject), person))
                .flatMap(o -> {
                    if (En_ExtAppType.JIRA.getCode().equals(caseObject.getExtAppType())) {
                        return caseService.updateCaseObjectMetaJira(authToken, new CaseObjectMetaJira(caseObject), person);
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


    @PostMapping(value = "/addyoutrackidintoissue/{youtrackId}/{caseNumber:[0-9]+}")
    public Result<Long> addYoutrackIdIntoIssue( HttpServletRequest request, HttpServletResponse response,
                                                @PathVariable("caseNumber") Long caseNumber,
                                                @PathVariable("youtrackId") String youtrackId ) {
        log.info( "addYoutrackIdIntoIssue() caseNumber={} youtrackId={}", caseNumber, youtrackId );

        return AuthUtils.authenticate(request, response, authService, sidGen, log).map( descripter -> descripter.makeAuthToken() ).flatMap( token ->
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

        return AuthUtils.authenticate(request, response, authService, sidGen, log).map( descripter -> descripter.makeAuthToken() ).flatMap( token ->
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
        return AuthUtils.authenticate(request, response, authService, sidGen, log).map( descripter -> descripter.makeAuthToken() ).flatMap( token ->
                caseLinkService.removeYoutrackLink( token, oldCaseNumber, youtrackId ).flatMap( aBoolean -> ok( token ) ) ).flatMap( token ->
                caseLinkService.addYoutrackLink( token, newCaseNumber, youtrackId ) )
                .ifOk( linkId -> log.info( "changeYoutrackIdInIssue(): OK" ) )
                .ifError( result -> log.warn( "changeYoutrackIdInIssue(): Can`t change youtrack id {} in case with number {}. status: {}",
                        youtrackId, oldCaseNumber, result ) );
    }


    private CaseQuery makeCaseQuery(CaseApiQuery apiQuery) {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, apiQuery.getSearchString(), apiQuery.getSortField(), apiQuery.getSortDir());
        query.setLimit(apiQuery.getLimit());
        query.setOffset(apiQuery.getOffset());
        // optional
        query.setStateIds(getCaseStateIdList(apiQuery.getStates()));
        query.setManagerIds(apiQuery.getManagerIds());
        query.setAllowViewPrivate(apiQuery.isAllowViewPrivate());
        query.setCreatedFrom(parseDate(apiQuery.getCreatedFrom()));
        query.setCreatedTo(parseDate(apiQuery.getCreatedTo()));
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