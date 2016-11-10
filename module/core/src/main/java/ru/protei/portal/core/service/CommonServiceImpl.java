package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.util.List;

/**
 * Created by bondarenko on 10.11.16.
 */
public class CommonServiceImpl implements CommonService {

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Override
    public CoreResponse<List<En_CaseState>> getStatesByCaseType(En_CaseType caseType) {
        List<En_CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if ( states == null )
            new CoreResponse<List<En_CaseState>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<En_CaseState>>().success(states);
    }
}
