package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_TableEntity;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class UserCaseAssignmentServiceImpl implements UserCaseAssignmentService {
    private final static Logger log = LoggerFactory.getLogger(UserCaseAssignmentServiceImpl.class);

    @Autowired
    CaseTagService caseTagService;
    @Autowired
    UserCaseAssignmentDAO userCaseAssignmentDAO;
    @Autowired
    CaseShortViewDAO caseShortViewDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CaseStateDAO caseStateDAO;
    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;
    @Autowired
    PortalConfig config;

    @Override
    @Transactional
    public Result<UserCaseAssignmentTable> saveTableEntity(AuthToken token, UserCaseAssignment userCaseAssignment) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!isValid(userCaseAssignment)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean isNew = userCaseAssignment.getId() == null;
        Long loginId = token.getUserLoginId();
        if (!isNew && !Objects.equals(userCaseAssignment.getLoginId(), loginId)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        userCaseAssignment.setLoginId(loginId);

        if (!userCaseAssignmentDAO.saveOrUpdate(userCaseAssignment)) {
            return error(isNew
                    ? En_ResultStatus.NOT_CREATED
                    : En_ResultStatus.NOT_UPDATED);
        }

        UserCaseAssignmentTable table = getUserCaseAssignmentTable(token, loginId);
        return ok(table);
    }

    @Override
    @Transactional
    public Result<UserCaseAssignmentTable> removeTableEntity(AuthToken token, Long id) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserCaseAssignment userCaseAssignment = userCaseAssignmentDAO.get(id);
        if (userCaseAssignment == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Long loginId = token.getUserLoginId();
        if (!Objects.equals(userCaseAssignment.getLoginId(), loginId)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!userCaseAssignmentDAO.removeByKey(id)) {
            return error(En_ResultStatus.NOT_REMOVED);
        }

        UserCaseAssignmentTable table = getUserCaseAssignmentTable(token, loginId);
        return ok(table);
    }

    @Override
    public Result<UserCaseAssignmentTable> getCaseAssignmentTable(AuthToken token) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Long loginId = token.getUserLoginId();
        UserCaseAssignmentTable table = getUserCaseAssignmentTable(token, loginId);
        return ok(table);
    }

    private UserCaseAssignmentTable getUserCaseAssignmentTable(AuthToken token, long loginId) {
        UserCaseAssignmentTable table = new UserCaseAssignmentTable();
        withUserCaseAssignments(table, loginId);
        withCaseViews(table, token);
        withCaseViewTags(table, token);
        return table;
    }

    private void withUserCaseAssignments(UserCaseAssignmentTable table, long loginId) {
        List<UserCaseAssignment> userCaseAssignments = userCaseAssignmentDAO.findByLoginId(loginId);
        fillStateEntityOptions(userCaseAssignments);
        fillPersonShortViews(userCaseAssignments);
        table.setUserCaseAssignments(userCaseAssignments);
    }

    private void withCaseViews(UserCaseAssignmentTable table, AuthToken token) {
        CaseQuery caseQuery = makeCaseQuery(table.getUserCaseAssignments(), token);
        long limit = config.data().getUiConfig().getIssueAssignmentDeskLimit();
        boolean isOverflow = false;
        List<CaseShortView> caseShortViews;
        if (caseQuery == null) {
            caseShortViews = new ArrayList<>();
        } else {
            Long total = caseShortViewDAO.count(caseQuery);
            if (total > limit) {
                caseQuery.setLimit(caseQuery.getOffset() + (int) limit);
                isOverflow = true;
            }
            caseShortViews = caseShortViewDAO.getSearchResult(caseQuery).getResults();
        }
        table.setCaseShortViews(caseShortViews);
        table.setCaseShortViewsLimit(limit);
        table.setCaseShortViewsLimitOverflow(isOverflow);
    }

    private void withCaseViewTags(UserCaseAssignmentTable table, AuthToken token) {
        List<CaseShortView> caseShortViews = table.getCaseShortViews();
        List<Long> caseIds = stream(caseShortViews)
                .map(CaseShortView::getId)
                .collect(Collectors.toList());
        if (isEmpty(caseIds)) {
            return;
        }
        caseTagService.getCaseObjectTags(token, caseIds)
                .ifError(result -> log.warn("Failed to fetch case tags | status='{}', message='{}'", result.getStatus(), result.getMessage()))
                .ifOk(tags -> assignTagsToCases(caseShortViews, tags));
        table.setCaseShortViews(caseShortViews);
    }

    private void fillStateEntityOptions(List<UserCaseAssignment> userCaseAssignments) {
        String query = userCaseAssignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.COLUMN)
                .map(UserCaseAssignment::getStates)
                .flatMap(Collection::stream) // flatten
                .distinct()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        if (StringUtils.isEmpty(query)) {
            return;
        }
        Map<Long, EntityOption> stateMap = CollectionUtils.stream(
                caseStateDAO.partialGetListByCondition(
                        "id IN (" + query + ")",
                        Collections.emptyList(),
                        "id", "STATE")
                )
                .collect(Collectors.toMap(
                        CaseState::getId,
                        caseState -> new EntityOption(caseState.getState(), caseState.getId()))
                );
        for (UserCaseAssignment userCaseAssignment : userCaseAssignments) {
            if (userCaseAssignment.getTableEntity() != En_TableEntity.COLUMN) {
                continue;
            }
            List<EntityOption> stateList = new ArrayList<>();
            for (Long id : userCaseAssignment.getStates()) {
                EntityOption p = stateMap.get(id);
                if (p != null) {
                    stateList.add(p);
                }
            }
            userCaseAssignment.setStateEntityOptions(stateList);
        }
    }

    private void fillPersonShortViews(List<UserCaseAssignment> userCaseAssignments) {
        String query = userCaseAssignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.ROW)
                .map(UserCaseAssignment::getPersons)
                .flatMap(Collection::stream) // flatten
                .distinct()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        if (StringUtils.isEmpty(query)) {
            return;
        }
        Map<Long, PersonShortView> personMap = stream(
                personDAO.partialGetListByCondition(
                    "id IN (" + query + ")",
                    Collections.emptyList(),
                    "id", "displayShortName", "isfired")
                )
                .collect(Collectors.toMap(
                        Person::getId,
                        person -> new PersonShortView(person.getDisplayShortName(), person.getId(), person.isFired())
                ));
        for (UserCaseAssignment userCaseAssignment : userCaseAssignments) {
            if (userCaseAssignment.getTableEntity() != En_TableEntity.ROW) {
                continue;
            }
            List<PersonShortView> personList = new ArrayList<>();
            for (Long id : userCaseAssignment.getPersons()) {
                PersonShortView p = personMap.get(id);
                if (p != null) {
                    personList.add(p);
                }
            }
            userCaseAssignment.setPersonShortViews(personList);
        }
    }

    private void assignTagsToCases(List<CaseShortView> caseViews, List<CaseObjectTag> tags) {
        for (CaseObjectTag tag : emptyIfNull(tags)) {
            CaseShortView caseView = findCaseView(caseViews, tag.getCaseId());
            assignTag(caseView, tag);
        }
    }

    private CaseShortView findCaseView(List<CaseShortView> caseViews, Long caseId) {
        for (CaseShortView caseView : emptyIfNull(caseViews)) {
            if (Objects.equals(caseView.getId(), caseId)) {
                return caseView;
            }
        }
        return null;
    }

    private void assignTag(CaseShortView caseView, CaseObjectTag caseTag) {
        if (caseView == null) {
            return;
        }
        List<CaseTag> tags = caseView.getTags();
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(caseTag.getTag());
        caseView.setTags(tags);
    }

    private CaseQuery makeCaseQuery(List<UserCaseAssignment> userCaseAssignments, AuthToken token) {
        if (CollectionUtils.isEmpty(userCaseAssignments)) {
            return null;
        }
        List<Long> stateIds = userCaseAssignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.COLUMN)
                .map(UserCaseAssignment::getStates)
                .flatMap(Collection::stream) // flatten
                .distinct()
                .collect(Collectors.toList());
        List<Long> managerIds = userCaseAssignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.ROW)
                .map(UserCaseAssignment::getPersons)
                .flatMap(Collection::stream) // flatten
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(stateIds) || CollectionUtils.isEmpty(managerIds)) {
            return null;
        }
        CaseQuery query = new CaseQuery();
        query.setStateIds(stateIds);
        query.setManagerIds(managerIds);
        if (!companyGroupHomeDAO.isHomeCompany(token.getCompanyId())) {
            query.setManagerCompanyIds(Collections.singletonList(token.getCompanyId()));
        }
        return query;
    }

    private boolean isValid(UserCaseAssignment userCaseAssignment) {
        switch (userCaseAssignment.getTableEntity()) {
            case COLUMN: {
                if (CollectionUtils.isEmpty(userCaseAssignment.getStates())) {
                    return false;
                }
                if (CollectionUtils.isNotEmpty(userCaseAssignment.getPersons())) {
                    return false;
                }
                break;
            }
            case ROW: {
                if (CollectionUtils.isEmpty(userCaseAssignment.getPersons())) {
                    return false;
                }
                if (CollectionUtils.isNotEmpty(userCaseAssignment.getStates())) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
}
