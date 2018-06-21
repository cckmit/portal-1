package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.List;

import static ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies.SELECTED;

public class CaseStateServiceImpl implements CaseStateService {

    @Inject
    private CaseStateDAO caseStateDAO;

    @Inject
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public CoreResponse<List<CaseState>> caseStateList(AuthToken authToken) {
        List<CaseState> list = caseStateDAO.getAll();

        if ( list == null )
            return new CoreResponse<List<CaseState>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseState>>().success(list);
    }

    @Override
    public CoreResponse<CaseState> getCaseState(AuthToken authToken, long id) {
        CaseState state = caseStateDAO.get(id);
        if (state == null)
            return new CoreResponse<CaseState>().error(En_ResultStatus.GET_DATA_ERROR);

        if (SELECTED.equals(state.getUsageInCompanies())) {
            jdbcManyRelationsHelper.fill(state, "companies");
        }

        return new CoreResponse<CaseState>().success(state);
    }
}
