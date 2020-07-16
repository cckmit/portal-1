package ru.protei.portal.app.portal.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

public interface PersonSubscriptionControllerAsync {

    void getPersonSubscriptions(AsyncCallback<Set<PersonShortView>> async);

    void updatePersonSubscriptions(Set<PersonShortView> persons, AsyncCallback<Set<PersonShortView>> async);
}
