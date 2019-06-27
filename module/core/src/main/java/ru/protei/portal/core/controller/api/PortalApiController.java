package ru.protei.portal.core.controller.api;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.controller.auth.SecurityDefs;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *  Севрис для  API
 */
@RestController
@RequestMapping(value = "/portal_api", headers = "Accept=application/json")
public class PortalApiController {

    @Autowired
    private AuthService authService;

    @Autowired
    CaseService caseService;

    private static final Logger log = LoggerFactory.getLogger( PortalApiController.class );

    /**
     * Получение списка обращений по параметрам
     * @return List<CaseShortView>
     */
    @PostMapping( value = "/caseList" )
    public APIResult<List<CaseShortView>> getCaseList(
            @RequestBody Long managerId,
            @RequestBody int limit,
            @RequestBody int offset,
            HttpServletRequest request,
            HttpServletResponse response) {

         log.debug("API | getCaseList(): manager={}, limit={}, offset={}", managerId, limit, offset);

        try {
            //@todo получение token по header Authorization
            Credentials cr = Credentials.parse(request.getHeader("Authorization"));
            if ((cr == null) || (!cr.isValid())) {
                authRequired(response, "Basic authentication required");
                return APIResult.error(En_ResultStatus.INVALID_LOGIN_OR_PWD, "Basic authentication required");
            }

            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader(SecurityDefs.USER_AGENT_HEADER);

            log.debug("API | Authentication: ip={}, user={}", ip, cr.login);
            CoreResponse<UserSessionDescriptor> result = authService.login(null, cr.login, cr.password, ip, userAgent);

            if (result.isError()) {
                return APIResult.error(result.getStatus(), result.getStatus().toString());
            }

            AuthToken authToken = result.getData().makeAuthToken();

            CoreResponse<SearchResult<CaseShortView>> searchList = caseService.getCaseObjects(
                    authToken, makeCaseQuery(managerId, limit, offset));

            if (result.isError())
                return APIResult.error(En_ResultStatus.INCORRECT_PARAMS, "Message error text");

            return APIResult.okWithData(searchList.getData().getResults());

        } catch (IllegalArgumentException | IOException ex) {
            return APIResult.error(En_ResultStatus.INCORRECT_PARAMS, "Some parameters are incorrect");
        } catch (Exception e) {
            e.printStackTrace();
            return APIResult.error(En_ResultStatus.INTERNAL_ERROR, "Message error text");
        }
    }

    private boolean authRequired(HttpServletResponse response, String logMessage) throws Exception {
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + logMessage + "\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    private CaseQuery makeCaseQuery(Long managerId, int limit, int offset) {
        CaseQuery query = new CaseQuery( En_CaseType.CRM_SUPPORT, null, En_SortField.issue_number, En_SortDir.ASC );
        query.setLimit(limit);
        query.setOffset(offset);
        query.setManagerIds(getManagersIdList(managerId));
        query.setStates(getStateList());
        return query;
    }

    private List<En_CaseState> getStateList() {
        List<En_CaseState> states = new ArrayList<>();
        states.add(En_CaseState.CREATED);
        states.add(En_CaseState.OPENED);
        states.add(En_CaseState.ACTIVE);
        states.add(En_CaseState.TEST_LOCAL);
        states.add(En_CaseState.WORKAROUND);
        states.add(En_CaseState.INFO_REQUEST);
        states.add(En_CaseState.CUST_PENDING);
        states.add(En_CaseState.TEST_CUST);
        return states;
    }

    private List< Long > getManagersIdList( Long managerID ) {
        if ( managerID == null ) {
            return null;
        }
        List<Long> managerIds = new ArrayList<>();
        managerIds.add(managerID);
        return managerIds;
    }

/*    public static Date parseDate(String date) {
        Date res;
        if (date == null)
            return null;
        if ((res  = doParseDate(date, "yyyy-MM-dd HH:mm:ss.S")) != null)
            return res;
        if ((res  = doParseDate(date, "yyyy-MM-dd HH:mm:ss")) != null)
            return res;
        if ((res  = doParseDate(date, "yyyy-MM-dd")) != null)
            return res;
        return null;
    }

    protected static Date doParseDate(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (Throwable ex) {
            return null;
        }
    }*/
}