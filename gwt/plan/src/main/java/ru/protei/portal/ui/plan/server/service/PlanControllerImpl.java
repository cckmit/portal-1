package ru.protei.portal.ui.plan.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.service.PlanService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.PlanController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Service( "PlanController" )
public class PlanControllerImpl implements PlanController {

    @Override
    public SearchResult<Plan> getPlanList(PlanQuery query) throws RequestFailedException {

        log.info( "getPlanList(): search={} | sortField={} | order={}",
                query.getSearchString(), query.getSortField(), query.getSortDir() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(planService.getPlans(token, query));
    }

    @Override
    public Plan getPlanWithIssues(Long planId) throws RequestFailedException {

        log.info( "getPlanWithIssues(): planId={}", planId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(planService.getPlanWithIssues(token, planId));
    }

    @Override
    public Long createPlan(Plan plan) throws RequestFailedException {

        log.info( "createPlan(): plan={}", plan);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(planService.createPlan(token, plan));
    }

    @Autowired
    private PlanService planService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(PlanControllerImpl.class);
}
