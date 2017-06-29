package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterActivity;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterView;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Активность списка компаний
 */
public abstract class CompanyListActivity implements AbstractCompanyListActivity, AbstractCompanyItemActivity, AbstractCompanyFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity( this );
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( CompanyEvents.Show event ) {
        fireEvent(new AppEvents.InitPanelName(lang.companies()));
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.COMPANY_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.COMPANY ) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();
        requestCompanies();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.COMPANY.equals( event.identity ) ) {
            return;
        }

        fireEvent(new CompanyEvents.Edit(null));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Override
    public void onCreateClicked( ) { fireEvent(new CompanyEvents.Edit()); }

    @Override
    public void onEditClicked( AbstractCompanyItemView itemView ) {
        Company value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new CompanyEvents.Edit ( value.getId() ));
    }

    @Override
    public void onPreviewClicked( AbstractCompanyItemView itemView ) {
        Company value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent(new CompanyEvents.ShowPreview(itemView.getPreviewContainer(), value));
        animation.showPreview(itemView, (IsWidget) itemView.getPreviewContainer());
    }

    @Override
    public void onFavoriteClicked(AbstractCompanyItemView itemView) {
        Window.alert("Clicked on favorite of company with id = " + itemViewToModel.get(itemView).getId() + "!");
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        requestCompanies();
    }

    private void requestCompanies() {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getChildContainer().clear();
        itemViewToModel.clear();

        companyService.getCompanies(query, new RequestCallback< List < Company > >() {

            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List< Company > companies ) {
                fillViewHandler = taskService.startPeriodicTask( companies, fillViewer, 50, 50 );
            }
        });
    }

    private CompanyQuery makeQuery() {
        query = new CompanyQuery(filterView.searchPattern().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC);

        if(filterView.categories().getValue() != null)
            query.setCategoryIds(
                    filterView.categories().getValue()
                            .stream()
                            .map( EntityOption::getId )
                            .collect( Collectors.toList() ));

        if(filterView.group().getValue() != null)
            query.setGroupId(filterView.group().getValue().getId());

        return query;
    };

    private AbstractCompanyItemView makeView( Company company ) {
        AbstractCompanyItemView itemView = factory.get();
        itemView.setActivity( this );
        itemView.setName( company.getCname() );
        itemView.setEditEnabled( policyService.hasPrivilegeFor( En_Privilege.COMPANY_EDIT ) );

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(company.getContactInfo());

        itemView.setPhone(infoFacade.allPhonesAsString());
        itemView.setEmail(infoFacade.allEmailsAsString());
        itemView.setWebsite(infoFacade.getWebSite() );

        CompanyCategory category = company.getCategory();
        if ( category != null ) {
            itemView.setType( En_CompanyCategory.findById( category.getId() ) );
        }
        return itemView;
    }

    Consumer< Company > fillViewer = new Consumer< Company >() {
        @Override
        public void accept( Company company ) {
            AbstractCompanyItemView itemView = makeView( company );

            itemViewToModel.put( itemView, company );
            view.getChildContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    AbstractCompanyListView view;
    @Inject
    AbstractCompanyFilterView filterView;
    @Inject
    Lang lang;

    @Inject
    Provider< AbstractCompanyItemView > factory;

    @Inject
    CompanyServiceAsync companyService;
    @Inject
    PlateListAnimation animation;
    @Inject
    PolicyService policyService;
    @Inject
    PeriodicTaskService taskService;
    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map< AbstractCompanyItemView, Company > itemViewToModel = new HashMap< AbstractCompanyItemView, Company >();
    private AppEvents.InitDetails init;
    private CompanyQuery query;

    private static String CREATE_ACTION;
}