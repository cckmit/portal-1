package ru.protei.portal.core.controller.api.json;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.controller.api.json.utils.JsonRequest;
import ru.protei.portal.core.controller.api.json.utils.JsonResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.portal.core.service.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

@RestController
@RequestMapping(value = "/jsonApi/company", headers = "Accept=application/json",
        produces = "application/json", consumes = "application/json")
@EnableWebMvc
public class CompanyApiController {
    private static final Logger log = LoggerFactory.getLogger(CompanyApiController.class);

    private final SessionService sessionService;
    private final CompanyService service;

    @Autowired
    public CompanyApiController(SessionService sessionService, CompanyService companyService) {
        this.sessionService = sessionService;
        this.service = companyService;
    }

    @PostMapping(value = "/getCompanyOptionListByQuery")
    @ApiOperation(value = "Get company option by query",
            notes = "Get list of company option filtered by query")
    public JsonResponse<List<EntityOption>> getCompanyOptionListByQuery(
            @ApiParam(value = "Company query", required = true)
            @RequestBody JsonRequest<CompanyQuery> query,
            HttpServletRequest request) {

        log.info("API | getCompanyOptionListByQuery(): Query={}", query);

        return new JsonResponse<>(query.getRequestId(),
                getAuthToken(request)
                        .flatMap(authToken -> service.companyOptionList(authToken, query.getData()))
        );
    }

    private Result<AuthToken> getAuthToken(HttpServletRequest request) {
        AuthToken authToken = sessionService.getAuthToken(request);
        if (authToken == null) {
            return error(En_ResultStatus.SESSION_NOT_FOUND);
        }
        return ok(authToken);
    }
}
