package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.PcbOrderDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class PcbOrderServiceImpl implements PcbOrderService {

    private static Logger log = LoggerFactory.getLogger(PcbOrderServiceImpl.class);

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    PcbOrderDAO pcbOrderDAO;
    @Autowired
    PolicyService policyService;

    @Override
    public Result<SearchResult<PcbOrder>> getPcbOrderList(AuthToken token, PcbOrderQuery query) {
        SearchResult<PcbOrder> sr = pcbOrderDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    @Transactional
    public Result<PcbOrder> removePcbOrder(AuthToken token, PcbOrder value) {
        if (value == null || value.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        PcbOrder pcbOrder = pcbOrderDAO.get(value.getId());
        if (pcbOrder == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        pcbOrderDAO.remove(pcbOrder);

        return ok(value);
    }
}
