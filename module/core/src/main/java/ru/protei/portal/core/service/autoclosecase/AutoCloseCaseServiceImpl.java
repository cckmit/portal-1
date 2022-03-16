package ru.protei.portal.core.service.autoclosecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;

import java.util.Date;
import java.util.List;

public class AutoCloseCaseServiceImpl implements AutoCloseCaseService {

    @Autowired
    CaseObjectDAO caseObjectDAO;

    private static final Logger log = LoggerFactory.getLogger(AutoCloseCaseServiceImpl.class);

    @Override
    @Transactional
    public void processAutoCloseByDeadLine() {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setAutoCLose(true);
        caseQuery.setDeadLine(new Date().getTime());
        List<CaseObject> caseObjects = caseObjectDAO.getCases(caseQuery);
        if (CollectionUtils.isNotEmpty(caseObjects)) {
            for (CaseObject caseObject : caseObjects) {
                caseObject.setStateId(CrmConstants.State.VERIFIED);
                if (!caseObjectDAO.partialMerge(caseObject, "auto_close")) {
                    log.error("Failed to close case object: {}", caseObject);
                }
                log.info("Case object: {} successfully close", caseObject);
            }
        }
    }
}
