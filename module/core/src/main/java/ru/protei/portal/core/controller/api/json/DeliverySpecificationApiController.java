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
import ru.protei.portal.core.model.ent.DeliverySpecification;
import ru.protei.portal.core.model.ent.DeliverySpecificationCreateRequest;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.portal.core.service.DeliverySpecificationService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

@RestController
@RequestMapping(value = "/jsonApi/deliverySpecification", headers = "Accept=application/json",
        produces = "application/json", consumes = "application/json")
@EnableWebMvc
public class DeliverySpecificationApiController {
    private static final Logger log = LoggerFactory.getLogger(DeliverySpecificationApiController.class);

    private final SessionService sessionService;
    private final DeliverySpecificationService service;

    @Autowired
    public DeliverySpecificationApiController(SessionService sessionService, DeliverySpecificationService deliveryService) {
        this.sessionService = sessionService;
        this.service = deliveryService;
    }

    @PostMapping(value = "/getDeliverySpecifications")
    @ApiOperation(value = "Get delivery specifications by query",
            notes = "Get search result of delivery specifications filtered by query")
    public JsonResponse<SearchResult<DeliverySpecification>> getDeliverySpecifications(
            @ApiParam(value = "Delivery specification query", required = true)
            @RequestBody JsonRequest<DeliverySpecificationQuery> query,
            HttpServletRequest request) {

        log.info("API | getDeliverySpecifications(): Query={}", query);

        return new JsonResponse<>(query.getRequestId(),
                getAuthToken(request)
                        .flatMap(authToken -> service.getDeliverySpecifications(authToken, query.getData()))
        );
    }

    @PostMapping(value = "/getDeliverySpecification")
    @ApiOperation(value = "Get delivery specification by ID")
    public JsonResponse<DeliverySpecification> getDeliverySpecification(
            @ApiParam(value = "Delivery specification ID", required = true)
            @RequestBody JsonRequest<Long> id,
            HttpServletRequest request) {

        log.info("API | getDeliverySpecification(): id={}", id);

        return new JsonResponse<>(id.getRequestId(),
                getAuthToken(request)
                        .flatMap(authToken -> service.getDeliverySpecification(authToken, id.getData()))
        );
    }

    @PostMapping(value = "/createDeliverySpecification")
    @ApiOperation(value = "Create delivery specification")
    public JsonResponse<DeliverySpecification> createDeliverySpecification(
            @ApiParam(value = "Delivery Specification", required = true)
            @RequestBody JsonRequest<DeliverySpecification> deliverySpecification,
            HttpServletRequest request) {

        log.info("API | createDeliverySpecification(): deliverySpecification={}", deliverySpecification);

        return new JsonResponse<>(deliverySpecification.getRequestId(),
                getAuthToken(request)
                        .flatMap(authToken -> service.createDeliverySpecification(authToken, deliverySpecification.getData()))
        );
    }

    @PostMapping(value = "/createDeliverySpecifications")
    @ApiOperation(value = "Create list of delivery specification")
    public JsonResponse<Boolean> createDeliverySpecifications(
            @ApiParam(value = "List of delivery specification", required = true)
            @RequestBody JsonRequest<List<DeliverySpecification>> deliverySpecificationList,
            HttpServletRequest request) {

        log.info("API | createDeliverySpecifications(): list={}", deliverySpecificationList);

        return new JsonResponse<>(deliverySpecificationList.getRequestId(),
                getAuthToken(request)
                        .flatMap(authToken -> service.createDeliverySpecifications(authToken, deliverySpecificationList.getData()))
        );
    }

    @PostMapping(value = "/importDeliverySpecifications")
    @ApiOperation(value = "Import list of delivery specification")
    public JsonResponse<Boolean> importDeliverySpecifications(
            @ApiParam(value = "Delivery Specification Create Request", required = true)
            @RequestBody JsonRequest<DeliverySpecificationCreateRequest> createRequest,
            HttpServletRequest request) {

        log.info("API | importDeliverySpecifications(): createRequest={}", createRequest);

        return new JsonResponse<>(createRequest.getRequestId(),
                getAuthToken(request)
                        .flatMap(authToken -> service.importDeliverySpecifications(authToken, createRequest.getData()))
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
