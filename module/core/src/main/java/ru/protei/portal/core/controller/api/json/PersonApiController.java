package ru.protei.portal.core.controller.api.json;

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
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.core.service.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

@RestController
@RequestMapping(value = "/jsonApi/person", headers = "Accept=application/json")
@EnableWebMvc
public class PersonApiController {
    private static final Logger log = LoggerFactory.getLogger(PersonApiController.class);

    private final SessionService sessionService;
    private final EmployeeService service;

    @Autowired
    public PersonApiController(SessionService sessionService, EmployeeService employeeService) {
        this.sessionService = sessionService;
        this.service = employeeService;
    }

    @PostMapping(value = "/getPersonShortViewListByQuery")
    public JsonResponse<List<PersonShortView>> getPersonShortViewListByQuery(
            @RequestBody JsonRequest<EmployeeQuery> q,
            HttpServletRequest request) {

        log.info("API | getPersonShortViewListByQuery(): Query={}", q);

        return new JsonResponse<>(q.getRequestId(),
                getAuthToken(request)
                        .flatMap(authToken -> service.shortViewList(q.getData()))
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
