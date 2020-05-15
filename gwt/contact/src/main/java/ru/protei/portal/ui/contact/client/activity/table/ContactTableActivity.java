package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
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
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterActivity;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
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

    @Event(Type.FILL_CONTENT)
    public void onShow( ContactEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.CONTACT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );
        view.showElements();

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.CONTACT_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, null, UiConstants.ActionBarIdentity.CONTACT ) :
                new ActionBarEvents.Clear()
        );

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.CONTACT.equals( event.identity ) ) {
            return;
        }

        view.clearSelection();

        fireEvent(new ContactEvents.Edit().newItem(filterView.company().getValue()));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.init = initDetails;
    }

    @Override
    public void onItemClicked(Person value) {
        persistScrollPosition();
        showPreview(value);
    }

    @Override
    public void onEditClicked(Person value ) {
        persistScrollPosition();
        fireEvent(ContactEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onRemoveClicked(Person value) {
        if (value != null) {
            fireEvent(new ConfirmDialogEvents.Show(lang.contactRemoveConfirmMessage(), lang.contactDelete(), removeAction(value.getId())));
        }
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected( int page ) {
        view.scrollTo(page);
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Person>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        marker = new Date().getTime();

        ContactQuery query = makeQuery();
        query.setOffset( offset );
        query.setLimit( limit );

        contactService.getContacts( query, new FluentCallback< SearchResult< Person> >()
                .withSuccess( sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if ( isFirstChunk ) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount( sr.getTotalCount() );
                        restoreScroll();
                    }
                })
        );
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
    }

    private void persistScrollPosition() {
        scrollTo = Window.getScrollTop();
    }

    private void showPreview ( Person value ) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new ContactEvents.ShowPreview( view.getPreviewContainer(), value ) );
        }
    }

    private ContactQuery makeQuery() {
        return new ContactQuery( filterView.company().getValue(),
                filterView.showFired().getValue() ? null : filterView.showFired().getValue(),
                false,
                filterView.searchPattern().getValue(), filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC );

    };

    private Runnable removeAction(Long contactId) {
        return () -> contactService.removeContact(contactId, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    fireEvent(new ContactEvents.Show(false));
                    fireEvent(new NotifyEvents.Show(lang.contactDeleted(), NotifyEvents.NotifyType.SUCCESS));
                } else {
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

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

    private AppEvents.InitDetails init;

    private static String CREATE_ACTION;

    private long marker;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
}
