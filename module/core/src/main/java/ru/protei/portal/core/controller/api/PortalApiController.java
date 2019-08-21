package ru.protei.portal.core.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.controller.auth.SecurityDefs;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.winter.core.utils.Pair;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public APIResult<List<CaseShortView>> getCaseList(
            @RequestBody CaseApiQuery query,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.debug("API | getCaseList(): query={}", query);

        try {
            APIResult<UserSessionDescriptor> userSessionDescriptorAPIResult = tryToAuthenticate(request, response);

            log.debug("========================== 1");


            if (userSessionDescriptorAPIResult.isFail()) {
                log.error("================================== Exception in get Case List : AUTH");
                return APIResult.error(userSessionDescriptorAPIResult.getStatus(), userSessionDescriptorAPIResult.getMessage());
            }

            log.debug("========================== 2");


            AuthToken authToken = userSessionDescriptorAPIResult.getData().makeAuthToken();

            log.debug("========================== 3");

            CoreResponse<SearchResult<CaseShortView>> searchList = caseService.getCaseObjects(authToken, makeCaseQuery(query));

            log.debug("========================== 4");

            return APIResult.okWithData(searchList.getData().getResults());

        } catch (IllegalArgumentException | IOException ex) {
            log.error(ex.getMessage());
            return APIResult.error(En_ResultStatus.INCORRECT_PARAMS, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error("================================== Exception in get Case List " + ex);
            return APIResult.error(En_ResultStatus.INTERNAL_ERROR, ex.getMessage());
        }
    }

    @PostMapping(value = "/cases/create")
    public APIResult<CaseObject> createCase(@RequestBody AuditableObject auditableObject,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        log.debug("API | createCase(): auditableObject={}", auditableObject);

        if (!(auditableObject instanceof CaseObject)) {
            return APIResult.error(En_ResultStatus.INCORRECT_PARAMS, "Incorrect AuditType");
        }

        try {
            APIResult<UserSessionDescriptor> userSessionDescriptorAPIResult = tryToAuthenticate(request, response);

            if (userSessionDescriptorAPIResult.isFail()) {
                return APIResult.error(userSessionDescriptorAPIResult.getStatus(), userSessionDescriptorAPIResult.getMessage());
            }

            AuthToken authToken = userSessionDescriptorAPIResult.getData().makeAuthToken();

            CoreResponse<CaseObject> caseObjectCoreResponse = caseService.saveCaseObject(
                    authToken,
                    (CaseObject) auditableObject,
                    userSessionDescriptorAPIResult.getData().getPerson()
            );

            if (caseObjectCoreResponse.isOk()) {
                return APIResult.okWithData(caseObjectCoreResponse.getData());
            } else {
                return APIResult.error(caseObjectCoreResponse.getStatus(), "Service Error");
            }

        } catch (IllegalArgumentException | IOException ex) {
            log.error(ex.getMessage());
            return APIResult.error(En_ResultStatus.INCORRECT_PARAMS, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return APIResult.error(En_ResultStatus.INTERNAL_ERROR, ex.getMessage());
        }
    }

    @PostMapping(value = "/cases/update")
    public APIResult<CaseObject> updateCase(@RequestBody AuditableObject auditableObject,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {

        log.debug("API | updateCase(): auditableObject={}", auditableObject);

        if (!(auditableObject instanceof CaseObject)) {
            return APIResult.error(En_ResultStatus.INCORRECT_PARAMS, "Incorrect AuditType");
        }

        try {
            APIResult<UserSessionDescriptor> userSessionDescriptorAPIResult = tryToAuthenticate(request, response);

            if (userSessionDescriptorAPIResult.isFail()) {
                return APIResult.error(userSessionDescriptorAPIResult.getStatus(), userSessionDescriptorAPIResult.getMessage());
            }

            AuthToken authToken = userSessionDescriptorAPIResult.getData().makeAuthToken();

            CoreResponse<CaseObject> caseObjectCoreResponse = caseService.updateCaseObject(
                    authToken,
                    (CaseObject) auditableObject,
                    userSessionDescriptorAPIResult.getData().getPerson()
            );

            if (caseObjectCoreResponse.isOk()) {
                return APIResult.okWithData(caseObjectCoreResponse.getData());
            } else {
                return APIResult.error(caseObjectCoreResponse.getStatus(), "Service Error");
            }

        } catch (IllegalArgumentException | IOException ex) {
            log.error(ex.getMessage());
            return APIResult.error(En_ResultStatus.INCORRECT_PARAMS, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return APIResult.error(En_ResultStatus.INTERNAL_ERROR, ex.getMessage());
        }
    }

    private CaseQuery makeCaseQuery(CaseApiQuery apiQuery) {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, apiQuery.getSearchString(), apiQuery.getSortField(), apiQuery.getSortDir());
        query.setLimit(apiQuery.getLimit());
        query.setOffset(apiQuery.getOffset());
        // optional
        query.setStateIds(getCaseStateIdList(apiQuery.getStates()));
        query.setManagerIds(apiQuery.getManagerIds());
        if (CollectionUtils.isEmpty(query.getManagerIds())) {
            query.setOrWithoutManager(true);
        }
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

    private APIResult<UserSessionDescriptor> tryToAuthenticate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Credentials cr = Credentials.parse(request.getHeader("Authorization"));

        if ((cr == null) || (!cr.isValid())) {
            String logMsg = "Basic authentication required";
            response.setHeader("WWW-Authenticate", "Basic realm=\"" + logMsg + "\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            log.error("API | {}", logMsg);
            return APIResult.error(En_ResultStatus.INVALID_LOGIN_OR_PWD, logMsg);
        }

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader(SecurityDefs.USER_AGENT_HEADER);
        log.debug("API | Authentication: ip={}, user={}", ip, cr.login);

        CoreResponse<UserSessionDescriptor> authResult = authService.login(sidGen.generateId(), cr.login, cr.password, ip, userAgent);

        if (authResult.isError()) {
            log.error("API | Authentification error {}", authResult.getStatus().name());
            return APIResult.error(authResult.getStatus(), "Authentification error");
        }

        return APIResult.okWithData(authResult.getData());
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