package ru.protei.portal.ui.contact.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContactServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterActivity;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterView;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.List;

/**
 * Активность таблицы контактов
 */
public abstract class ContactTableActivity implements AbstractContactTableActivity, AbstractContactFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( ContactEvents.Show event ) {
        this.fireEvent( new AppEvents.InitPanelName( lang.contacts() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fireEvent( new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.CONTACT ) );

        view.showElements();
        isShowTable = false;

        ContactQuery query = makeQuery( null );

        requestContacts( query );
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
/*        if ( !CREATE_ACTION.equals( event.identity ) ) {
            return;
        }*/

        if ( !UiConstants.ActionBarIdentity.CONTACT.equals( event.identity ) ) {
            return;
        }

        //fireEvent(new ContactEvents.Edit());
        fireEvent(new ContactEvents.Edit().newItem(filterView.company().getValue()));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShowTable( ContactEvents.ShowTable event ) {

        event.parent.clear();
        event.parent.add( view.asWidget() );
        view.hideElements();
        isShowTable = true;

        ContactQuery query = makeQuery( event.companyId );

        requestContacts( query );
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
    public void onFilterChanged() {
        ContactQuery query = makeQuery( null );
        requestContacts(query);
    }

    private void requestContacts( ContactQuery query ) {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.clearRecords();
        animation.closeDetails();


        contactService.getContacts(query, new RequestCallback<List<Person>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Person> persons) {
                fillViewHandler = taskService.startPeriodicTask(persons, p -> view.addRecord(p), 50, 50);
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
        return new ContactQuery( filterView.company().getValue(),
                filterView.showFired().getValue() ? null : filterView.showFired().getValue(),
                filterView.searchPattern().getValue(), filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC );

    };

    @Inject
    Lang lang;

    @Inject
    AbstractContactTableView view;
    @Inject
    AbstractContactFilterView filterView;

    @Inject
    ContactServiceAsync contactService;

    @Inject
    TableAnimation animation;

    @Inject
    PeriodicTaskService taskService;

    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private boolean isShowTable = false;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
}
