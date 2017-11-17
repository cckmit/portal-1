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
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Активность списка компаний
 */
public abstract class CompanyListActivity implements Activity, AbstractCompanyListActivity, AbstractCompanyItemActivity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onShow( CompanyEvents.ShowDefinite event ) {
        if(event.viewType != ViewType.LIST)
            return;

        this.query = event.query;
        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getFilterContainer().add(event.filter);

        requestCompanies();
    }

    @Override
    public void onFavoriteClicked(AbstractCompanyItemView itemView) {
        Window.alert("Clicked on favorite of company with id = " + itemViewToModel.get(itemView).getId() + "!");
    }

    @Override
    public void onPreviewClicked( AbstractCompanyItemView itemView ) {
        Company value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent(new CompanyEvents.ShowPreview(itemView.getPreviewContainer(), value, false));
        animation.showPreview(itemView, (IsWidget) itemView.getPreviewContainer());
    }

    @Override
    public void onEditClicked( AbstractCompanyItemView itemView ) {
        Company value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new CompanyEvents.Edit ( value.getId() ));
    }

    @Override
    public void onCreateClicked( ) { fireEvent(new CompanyEvents.Edit()); }

    private AbstractCompanyItemView makeView(Company company ) {
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

    private void requestCompanies() {
        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getChildContainer().clear();
        view.setListCreateBtnVisible(policyService.hasPrivilegeFor( En_Privilege.COMPANY_CREATE ));
        itemViewToModel.clear();

        companyService.getCompanies(query, new RequestCallback< List< Company > >() {

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

    Consumer< Company > fillViewer = new Consumer< Company >() {
        @Override
        public void accept( Company company ) {
            AbstractCompanyItemView itemView = makeView( company );

            itemViewToModel.put( itemView, company );
            view.getChildContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    PlateListAnimation animation;
    @Inject
    AbstractCompanyListView view;
    @Inject
    Provider< AbstractCompanyItemView > factory;
    @Inject
    PeriodicTaskService taskService;
    @Inject
    CompanyServiceAsync companyService;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private PeriodicTaskService.PeriodicTaskHandler fillViewHandler;
    private AppEvents.InitDetails init;
    private CompanyQuery query;
    private Map< AbstractCompanyItemView, Company > itemViewToModel = new HashMap< AbstractCompanyItemView, Company >();

}
