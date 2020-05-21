package ru.protei.portal.core.service;

import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.List;

import static ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies.SELECTED;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
public class CaseStateServiceImpl implements CaseStateService {

    @Inject
    private CaseStateDAO caseStateDAO;

    @Inject
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<List<CaseState>> caseStateList(AuthToken authToken, En_CaseType type) {
        List<CaseState> list = caseStateDAO.getAllByCaseType(type);

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(list);
    }

    @Override
    public Result<List<CaseState>> getCaseStatesOmitPrivileges(AuthToken authToken) {
        return caseStateList(authToken, En_CaseType.CRM_SUPPORT);
    }

    @Override
    public Result<CaseState> getCaseState(AuthToken authToken, long id) {
        CaseState state = caseStateDAO.get(id);
        if (state == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        if (SELECTED.equals(state.getUsageInCompanies())) {
            jdbcManyRelationsHelper.fill(state, "companies");
        }

        return ok(state);
    }

    @Override
    @Transactional
    public Result saveCaseState(AuthToken authToken, CaseState state) {
        if (state == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        Long id = caseStateDAO.persist(state);

        if (id == null) {
            throw new RuntimeException("Can't create case state. DAO return null id.");
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
            throw new RuntimeException("Can't update case state. DAO return false.");
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
}
