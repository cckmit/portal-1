package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.List;

public class CaseStateServiceImpl implements CaseStateService {

    @Inject
    private CaseStateDAO caseStateDAO;

    @Inject
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public CoreResponse<List<CaseState>> caseStateList() {
        List<CaseState> list = caseStateDAO.getAll();

        if ( list == null )
            return new CoreResponse<List<CaseState>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseState>>().success(list);
    }

    @Override
    public CoreResponse<CaseState> getCaseState(long id) {
        CaseState state = caseStateDAO.get(id);
        jdbcManyRelationsHelper.fill( state, "companies" );
        return new CoreResponse<CaseState>().success(state);
    }
}
