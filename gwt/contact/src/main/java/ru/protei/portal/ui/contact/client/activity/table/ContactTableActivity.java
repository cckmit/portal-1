package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContactControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterActivity;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterView;

import java.util.List;

/**
 * Активность таблицы контактов
 */
public abstract class ContactTableActivity
        implements AbstractContactTableActivity, AbstractContactFilterActivity,
        AbstractPagerActivity, Activity
{

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        view.setAnimation( animation );

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );

        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( ContactEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.contacts() ) );
        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.CONTACT_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.CONTACT ) :
                new ActionBarEvents.Clear()
        );

        view.showElements();
        isShowTable = false;
        contactId = null;

        query = makeQuery( null );
        requestTotalCount();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.CONTACT.equals( event.identity ) ) {
            return;
        }

        fireEvent(new ContactEvents.Edit().newItem(filterView.company().getValue()));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    @Event
    public void onConfirmRemove( ConfirmDialogEvents.Confirm event ) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        if (contactId == null) {
            return;
        }

        contactService.removeContact(contactId, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                contactId = null;
                if (result) {
                    fireEvent(new ContactEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.contactDeleted(), NotifyEvents.NotifyType.SUCCESS));
                } else {
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    @Event
    public void onCancelRemove( ConfirmDialogEvents.Cancel event ) {
        contactId = null;
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
    public void onRemoveClicked(Person value) {
        if (value != null) {
            contactId = value.getId();
            fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.contactRemoveConfirmMessage()));
        }
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

    @Override
    public void onPageChanged( int page ) {
        pagerView.setCurrentPage( page+1 );
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo( 0 );
    }

    @Override
    public void onLastClicked() {
        view.scrollTo( view.getPageCount()-1 );
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
                pagerView.setTotalPages( view.getPageCount() );
                pagerView.setTotalCount( count );
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
            return new ContactQuery( companyId, null, false, null, En_SortField.person_full_name, En_SortDir.ASC);
        }
        return new ContactQuery( filterView.company().getValue(),
                filterView.showFired().getValue() ? null : filterView.showFired().getValue(),
                false,
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
    ContactControllerAsync contactService;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    PolicyService policyService;

    private boolean isShowTable = false;
    private Long contactId = null;

    private AppEvents.InitDetails init;
    private ContactQuery query;

    private static String CREATE_ACTION;
}
