package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.RFIDDeviceDAO;
import ru.protei.portal.core.model.dao.RFIDLabelDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RFIDDevice;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.ent.RFIDLabel.Columns.LAST_SCAN_DATE;
import static ru.protei.portal.core.model.ent.RFIDLabel.Columns.RFID_DEVICE_ID;

public class RFIDLabelServiceImpl implements RFIDLabelService {

    private static final Logger log = LoggerFactory.getLogger(RFIDLabelServiceImpl.class);

    @Autowired
    RFIDLabelDAO labelDAO;
    @Autowired
    RFIDDeviceDAO deviceDAO;

    @Override
    public Result<RFIDLabel> get(AuthToken token, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(labelDAO.get(id));
    }

    @Override
    public Result<SearchResult<RFIDLabel>> getByQuery(AuthToken token, RFIDLabelQuery query) {
        return ok(labelDAO.getSearchResultByQuery(query));
    }

    @Override
    @Transactional
    public Result<RFIDLabel> update(AuthToken token, RFIDLabel value) {
        if (value == null || value.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RFIDLabel oldMeta = labelDAO.get(value.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        labelDAO.merge(value);
        return ok(value);
    }

    @Override
    @Transactional
    public Result<RFIDLabel> remove(AuthToken token, RFIDLabel value) {
        if (value == null || value.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RFIDLabel oldMeta = labelDAO.get(value.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        labelDAO.remove(value);
        return ok(value);
    }

    @Override
    @Transactional
    public Result<RFIDDevice> getOrCreateDeviceByReaderId(AuthToken token, String readerId) {
        if (readerId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        RFIDDevice byReaderId = deviceDAO.getByReaderId(readerId);
        if (byReaderId == null) {
            byReaderId = new RFIDDevice(readerId);
            deviceDAO.persist(byReaderId);
        }
        return ok(byReaderId);
    }

    @Override
    @Transactional
    public Result<RFIDLabel> saveOrUpdateLastScan(AuthToken token, RFIDLabel value) {
        if (value == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RFIDLabel byEPC = labelDAO.getByEPC(value.getEpc());
        if (byEPC == null) {
            labelDAO.persist(value);
        } else {
            if (value.getLastScanDate().after(byEPC.getLastScanDate())) {
                value.setId(byEPC.getId());
                labelDAO.partialMerge(value, RFID_DEVICE_ID, LAST_SCAN_DATE);
            }
        }

        return ok(value);
    }
}
