package ru.protei.portal.app.portal.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.Set;

@RemoteServiceRelativePath( "springGwtServices/PersonSubscriptionController" )
public interface PersonSubscriptionController extends RemoteService  {

    Set<PersonShortView> getPersonSubscriptions() throws RequestFailedException;

    Set<PersonShortView> updatePersonSubscriptions(Set<PersonShortView> persons) throws RequestFailedException;
}
