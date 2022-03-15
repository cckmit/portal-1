package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies.SELECTED;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
public class CaseStateServiceImpl implements CaseStateService {

    @Inject
    private CaseStateDAO caseStateDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Inject
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<List<CaseState>> getCaseStates(AuthToken authToken, En_CaseType type) {
        return getCaseStates(type);
    }

    @Override
    public Result<List<CaseState>> getCaseStatesOmitPrivileges(En_CaseType type) {
        return getCaseStates(type);
    }

    @Override
    public Result<List<CaseState>> getCaseStatesWithViewOrderOmitPrivileges(En_CaseType caseType) {
        List<CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if (states == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(states);
    }

    @Override
    public Result<CaseState> getCaseState(AuthToken authToken, long id) {
        CaseState state = caseStateDAO.get(id);

        if (state == null)
            return error(En_ResultStatus.NOT_FOUND);

        if (SELECTED.equals(state.getUsageInCompanies())) {
            jdbcManyRelationsHelper.fill(state, "companies");
        }

        return ok(state);
    }

    @Override
    public Result<CaseState> getCaseStateWithoutCompaniesOmitPrivileges(long id) {
        CaseState state = caseStateDAO.get(id);

        if (state == null)
            return error(En_ResultStatus.NOT_FOUND);

        return ok(state);
    }

    @Override
    @Transactional
    public Result<CaseState> createCaseState(AuthToken authToken, CaseState state) {
        if (state == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        Long id = caseStateDAO.persist(state);

        if (id == null) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED, "Can't create case state. DAO return null id.");
        }

        jdbcManyRelationsHelper.persist(state, "companies");

        return ok(state);
    }

    @Override
    @Transactional
    public Result<CaseState> updateCaseState(AuthToken authToken, CaseState state) {
        if (state == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        if (!caseStateDAO.merge(state)) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED, "Can't update case state. DAO return false.");
        }

        jdbcManyRelationsHelper.persist(state, "companies");

        return ok(state);
    }

    @Override
    public Result<List<CaseState>> getCaseStatesForCompanyOmitPrivileges(Long companyId) {
        if (companyId == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        List<CaseState> caseStates = caseStateDAO.getCaseStatesForCompany(companyId);

        return ok(caseStates);
    }

    private Result<List<CaseState>> getCaseStates(En_CaseType type) {
        List<CaseState> list = caseStateDAO.getAllByCaseType(type);

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(list);
    }

    @Override
    public Result<CaseState> getCaseStateByCaseIdOmitPrivileges(long caseId) {
        CaseState caseState = caseStateDAO.getCaseStateByCaseId(caseId);

        if (caseState == null)
            return error(En_ResultStatus.NOT_FOUND);

        return ok(caseState);
    }

    @Override
    public Result<List<CaseState>> getCaseStatesByIds(List<Long> caseStatesIds) {
        List<CaseState> caseStates = caseStateDAO.getListByKeys(caseStatesIds);

        if (caseStates == null)
            return error(En_ResultStatus.NOT_FOUND);

        return ok(caseStates);
    }
}
