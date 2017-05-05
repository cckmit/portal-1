package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.WorkingGroupDAO;
import ru.protei.portal.core.model.ent.WorkingGroup;

import java.util.List;

/**
 * Created by michael on 05.05.17.
 */
public class WorkingGroupServiceImpl implements WorkingGroupService {

    private static Logger logger = LoggerFactory.getLogger(WorkingGroupServiceImpl.class);

    @Autowired
    WorkingGroupDAO workingGroupDAO;

    @Override
    public CoreResponse<List<WorkingGroup>> getAll() {
        logger.debug("get all working groups");
        return new CoreResponse<List<WorkingGroup>>().success(workingGroupDAO.getAll());
    }
}
