package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.WorkingGroup;
import ru.protei.portal.ui.common.client.service.WorkingGroupService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Created by michael on 05.05.17.
 */
@Service("WorkingGroupService")
public class WorkingGroupServiceImpl implements WorkingGroupService {
    @Override
    public List<WorkingGroup> getAllGroups() throws RequestFailedException {
        log.debug("get all groups");

        CoreResponse<List<WorkingGroup>> result = workingGroupService.getAll();

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }


    @Autowired
    private ru.protei.portal.core.service.WorkingGroupService workingGroupService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
