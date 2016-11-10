package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.List;

/**
 * Created by bondarenko on 10.11.16.
 */
public interface CommonService {

    CoreResponse<List<En_CaseState>> getStatesByCaseType(En_CaseType caseType);

}
