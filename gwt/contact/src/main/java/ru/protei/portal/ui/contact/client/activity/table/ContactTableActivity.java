package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Активность таблицы контактов
 */
public abstract class ContactTableActivity implements AbstractContactTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setAnimation ( animation );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        view.resetFilter();
    }

    @Event
    public void onShow( ContactEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.contacts() ) );
        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.showElements();
        isShowTable = false;

        query = makeQuery( null );
        requestTotalCount();
    }

    @Event
    public void onShowTable( ContactEvents.ShowTable event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );
        view.hideElements();
        isShowTable = true;

        query = makeQuery( event.companyId );

        requestTotalCount();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    @Override
    public void onItemClicked ( Person value ) {
        if ( !isShowTable ) {
            showPreview( value );
        }
    }

    @Override
    public void onEditClicked(Person value ) {
        fireEvent(ContactEvents.Edit.byId(value.getId()));
    }


    @Override
    public void onCreateClick() {
        fireEvent(ContactEvents.Edit.newItem(view.company().getValue()));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery( null );
        requestTotalCount();
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<Person>> asyncCallback ) {
        query.setOffset( offset );
        query.setLimit( limit );

        contactService.getContacts( query, new RequestCallback<List<Person>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                asyncCallback.onFailure( throwable );
            }

            @Override
            public void onSuccess( List<Person> persons ) {
                asyncCallback.onSuccess( persons );
            }
        } );
    }

    private void requestTotalCount() {
        view.clearRecords();
        animation.closeDetails();

        contactService.getContactsCount(query, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long count) {
                view.setRecordCount( count );
            }
        });
    }

    private void showPreview ( Person value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new ContactEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    private ContactQuery makeQuery( Long companyId ) {
        if ( companyId != null ) {
            return new ContactQuery( companyId, null, null, En_SortField.person_full_name, En_SortDir.ASC);
        }
        return new ContactQuery( view.company().getValue(),
                view.showFired().getValue() ? null : view.showFired().getValue(),
                view.searchPattern().getValue(), view.sortField().getValue(),
                view.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC );

    };


    @Inject
    Lang lang;

    @Inject
    AbstractContactTableView view;

    @Inject
    ContactServiceAsync contactService;

    @Inject
    TableAnimation animation;

    private boolean isShowTable = false;

    private AppEvents.InitDetails init;
    private ContactQuery query;
}
