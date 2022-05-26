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
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DeliverySpecification;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.portal.core.service.DeliverySpecificationService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
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
            notes = "Get search result with delivery specifications filtered by query")
    public JsonResponse<SearchResult<DeliverySpecification>> getDeliverySpecifications(
            @ApiParam(value = "Delivery specification query", required = true)
            @RequestBody JsonRequest<DeliverySpecificationQuery> query,
            HttpServletRequest request) {

        log.info("API | getDeliverySpecifications(): Query={}", query);

        return new JsonResponse<>(query.getRequestId(),
                getFakeAuthToken(request)
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
                getFakeAuthToken(request)
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
                getFakeAuthToken(request)
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
                getFakeAuthToken(request)
                        .flatMap(authToken -> service.createDeliverySpecifications(authToken, deliverySpecificationList.getData()))
        );
    }

    private Result<AuthToken> getAuthToken(HttpServletRequest request) {
        AuthToken authToken = sessionService.getAuthToken(request);
        if (authToken == null) {
            return error(En_ResultStatus.SESSION_NOT_FOUND);
        }
        return ok(authToken);
    }

    private Result<AuthToken> getFakeAuthToken(HttpServletRequest request) {
        AuthToken token = new AuthToken("test-session-id");
        token.setIp("127.0.0.1");
        token.setUserLoginId(15550L);
        token.setPersonId(7777L);
        token.setPersonDisplayShortName("DeliverySpecificationApiController");
        token.setCompanyId(1L);
        token.setCompanyAndChildIds(null);
        token.setRoles(makeRoles());
        return ok(token);
    }

    private HashSet<UserRole> makeRoles() {
        UserRole role = new UserRole();
        role.setPrivileges(new HashSet<>(Arrays.asList(PRIVILEGES)));
        role.setScope(En_Scope.SYSTEM);
        return new HashSet<>(Arrays.asList(role));
    }

    private static final En_Privilege[] PRIVILEGES = new En_Privilege[] {
            En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE,
            En_Privilege.PRODUCT_VIEW, En_Privilege.PRODUCT_EDIT, En_Privilege.PRODUCT_CREATE,
            En_Privilege.COMPANY_VIEW, En_Privilege.COMPANY_EDIT, En_Privilege.COMPANY_CREATE,
            En_Privilege.CONTACT_VIEW, En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE,
            En_Privilege.EMPLOYEE_VIEW, En_Privilege.PROJECT_CREATE,
            En_Privilege.SUBNET_VIEW, En_Privilege.SUBNET_CREATE,
            En_Privilege.SUBNET_EDIT, En_Privilege.SUBNET_REMOVE,
            En_Privilege.RESERVED_IP_VIEW, En_Privilege.RESERVED_IP_CREATE,
            En_Privilege.RESERVED_IP_EDIT, En_Privilege.RESERVED_IP_REMOVE,
            En_Privilege.PLAN_CREATE, En_Privilege.PLAN_EDIT, En_Privilege.PLAN_REMOVE, En_Privilege.PLAN_VIEW,
            En_Privilege.PROJECT_CREATE, En_Privilege.PROJECT_EDIT, En_Privilege.PROJECT_REMOVE, En_Privilege.PROJECT_VIEW,
            En_Privilege.SITE_FOLDER_CREATE, En_Privilege.SITE_FOLDER_REMOVE, En_Privilege.SITE_FOLDER_EDIT,
            En_Privilege.ABSENCE_CREATE,
            En_Privilege.DOCUMENT_CREATE, En_Privilege.DOCUMENT_REMOVE,
            En_Privilege.DELIVERY_CREATE,
            En_Privilege.CASE_STATES_VIEW,
            En_Privilege.CONTRACT_VIEW,
            En_Privilege.DELIVERY_VIEW,
            En_Privilege.DELIVERY_SPECIFICATION_VIEW, En_Privilege.DELIVERY_SPECIFICATION_CREATE
    };
}
