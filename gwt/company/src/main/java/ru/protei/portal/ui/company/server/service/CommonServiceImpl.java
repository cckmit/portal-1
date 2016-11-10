package ru.protei.portal.ui.company.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.service.CommonService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Created by bondarenko on 10.11.16.
 */

@Service( "CommonService" )
public class CommonServiceImpl implements CommonService {

    @Override
    public List<En_CaseState> getStatesByCaseType(En_CaseType caseType) throws RequestFailedException {

        log.debug( "getStatesByCaseType: caseType={} ", caseType );

        CoreResponse< List<En_CaseState> > result = commonService.getStatesByCaseType(caseType);

        log.debug("result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    @Autowired
    private ru.protei.portal.core.service.CommonService commonService;

    private static final Logger log = LoggerFactory.getLogger("web");

}
