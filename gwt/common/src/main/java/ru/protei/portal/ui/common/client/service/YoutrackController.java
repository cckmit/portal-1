package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с yt сущностями
 */
@RemoteServiceRelativePath( "springGwtServices/YoutrackController" )
public interface YoutrackController extends RemoteService {
    List<YoutrackProject> getProjects(int offset, int limit) throws RequestFailedException;
}
