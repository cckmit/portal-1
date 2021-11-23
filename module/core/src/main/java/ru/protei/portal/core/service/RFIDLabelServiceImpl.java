package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.RFIDLabelDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class RFIDLabelServiceImpl implements RFIDLabelService {

    private static final Logger log = LoggerFactory.getLogger(RFIDLabelServiceImpl.class);

    @Autowired
    RFIDLabelDAO rfidLabelDAO;

    @Override
    public Result<RFIDLabel> get(AuthToken token, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(rfidLabelDAO.get(id));
    }

    @Override
    public Result<SearchResult<RFIDLabel>> getByQuery(AuthToken token, RFIDLabelQuery query) {
        return ok(rfidLabelDAO.getSearchResultByQuery(query));
    }

    @Override
    public Result<RFIDLabel> update(AuthToken token, RFIDLabel value) {
        if (value == null || value.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RFIDLabel oldMeta = rfidLabelDAO.get(value.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        rfidLabelDAO.merge(value);
        return ok(value);
    }

    @Override
    public Result<RFIDLabel> remove(AuthToken token, RFIDLabel value) {
        if (value == null || value.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RFIDLabel oldMeta = rfidLabelDAO.get(value.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        rfidLabelDAO.remove(value);
        return ok(value);
    }
}
