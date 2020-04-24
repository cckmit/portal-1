package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserCaseAssignmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_TableEntity;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class UserCaseAssignmentServiceImpl implements UserCaseAssignmentService {

    @Override
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

        UserCaseAssignmentTable table = getUserCaseAssignmentTable(loginId);
        return ok(table);
    }

    @Override
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

        UserCaseAssignmentTable table = getUserCaseAssignmentTable(loginId);
        return ok(table);
    }

    @Override
    public Result<UserCaseAssignmentTable> getCaseAssignmentTable(AuthToken token) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Long loginId = token.getUserLoginId();
        UserCaseAssignmentTable table = getUserCaseAssignmentTable(loginId);
        return ok(table);
    }

    private UserCaseAssignmentTable getUserCaseAssignmentTable(long loginId) {
        List<UserCaseAssignment> userCaseAssignments = userCaseAssignmentDAO.findByLoginId(loginId);
        fillPersonShortViews(userCaseAssignments);
        CaseQuery caseQuery = makeCaseQuery(userCaseAssignments);
        List<CaseShortView> caseShortViews = caseQuery == null
                ? new ArrayList<>()
                : caseShortViewDAO.getSearchResult(caseQuery).getResults();
        return new UserCaseAssignmentTable(userCaseAssignments, caseShortViews);
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
        Map<Long, PersonShortView> personMap = CollectionUtils.stream(
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

    private CaseQuery makeCaseQuery(List<UserCaseAssignment> userCaseAssignments) {
        if (CollectionUtils.isEmpty(userCaseAssignments)) {
            return null;
        }
        List<Long> stateIds = userCaseAssignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.COLUMN)
                .map(UserCaseAssignment::getStates)
                .flatMap(Collection::stream) // flatten
                .map(en_caseState -> (long)en_caseState.getId())
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

    @Autowired
    UserCaseAssignmentDAO userCaseAssignmentDAO;
    @Autowired
    CaseShortViewDAO caseShortViewDAO;
    @Autowired
    PersonDAO personDAO;
}
