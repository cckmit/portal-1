package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.Objects;

/**
 * Модель контактов домашней компании
 */
public abstract class EmployeeModel implements Activity,
        AsyncSelectorModel<PersonShortView> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        requestCurrentPerson(event.profile.getId());
        cache.clearCache();
        employeeQuery = new EmployeeQuery( null, false, true, En_SortField.person_full_name, En_SortDir.ASC );
        cache.setLoadHandler(makeLoadHandler(employeeQuery));
    }

    @Override
    public PersonShortView get( int elementIndex, LoadingHandler loadingHandler ) {
        if (currentPerson == null) {
            return cache.get( elementIndex, loadingHandler );
        }
        if (elementIndex == 0) return currentPerson;
        PersonShortView personShortView = cache.get( --elementIndex, loadingHandler );
        if (Objects.equals(personShortView, currentPerson)) {
            return cache.get( ++elementIndex, loadingHandler );
        }
        return personShortView;
    }

    public void clear() {
        cache.clearCache();
    }

    public void setAccounting(boolean isAccounting) {
        employeeQuery.setAccounting(isAccounting);
    }

    private SelectorDataCacheLoadHandler<PersonShortView> makeLoadHandler( EmployeeQuery query ) {
        return new SelectorDataCacheLoadHandler() {
            @Override
            public void loadData( int offset, int limit, AsyncCallback handler ) {
                query.setOffset(offset);
                query.setLimit(limit);
                employeeService.getEmployeeViewList( query, new RequestCallback<List<PersonShortView>>() {
                    @Override
                    public void onError( Throwable throwable ) {
                        fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                    }

                    @Override
                    public void onSuccess( List<PersonShortView> options ) {
                        handler.onSuccess( transliteration( options ) );
                    }
                } );
            }
        };
    }

    private void requestCurrentPerson(Long myId) {
        if(currentPerson!=null && Objects.equals(currentPerson.getId(), myId)){
            return;
        }
        currentPerson = null;
        personService.getPersonShortView(myId, new FluentCallback<PersonShortView>().withSuccess(r->currentPerson=r));
    }

    private List<PersonShortView> transliteration(List<PersonShortView> options) {
        return options;
    }

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    PersonControllerAsync personService;

    @Inject
    Lang lang;

    PersonShortView currentPerson;
    EmployeeQuery employeeQuery;
    private SelectorDataCache<PersonShortView> cache = new SelectorDataCache<>();
}
