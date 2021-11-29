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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
            lastScanRfidLabel.offer(value);
        } else {
            if (value.getLastScanDate().after(byEPC.getLastScanDate())) {
                value.setId(byEPC.getId());
                labelDAO.partialMerge(value, RFID_DEVICE_ID, LAST_SCAN_DATE);
                lastScanRfidLabel.offer(value);
            }
        }
        return ok(value);
    }

    @Override
    public Result<RFIDLabel> getLastScanLabel(AuthToken token, boolean start) {
        if (start) {
            lastScanRfidLabel.clear();
        }
        return ok(lastScanRfidLabel.poll());
    }

    static private final BlockingQueue<RFIDLabel> lastScanRfidLabel = new ArrayBlockingQueue<>(1);
}
