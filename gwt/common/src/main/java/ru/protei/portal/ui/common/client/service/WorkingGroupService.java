package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.WorkingGroup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Created by michael on 05.05.17.
 */
@RemoteServiceRelativePath( "springGwtServices/WorkingGroupService" )
public interface WorkingGroupService extends RemoteService{

    List<WorkingGroup> getAllGroups () throws RequestFailedException;

}
