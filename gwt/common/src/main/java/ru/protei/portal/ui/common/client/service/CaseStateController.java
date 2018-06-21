package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления Статусами обращений
 */
@RemoteServiceRelativePath("springGwtServices/CaseStateController")
public interface CaseStateController extends RemoteService {

    List<CaseState> getCaseStates() throws Exception;
}
