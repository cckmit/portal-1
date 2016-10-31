package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PeriodicTaskService;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Активность списка компаний
 */
public abstract class CompanyListActivity implements AbstractCompanyListActivity, AbstractCompanyItemActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        view.resetFilter();
    }

    @Event
    public void onShow( CompanyEvents.Show event ) {
        fireEvent( new AppEvents.InitPanelName( lang.companies() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        view.getChildContainer().clear();
        initCompanies();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    public void onFilterChanged() {
        view.getChildContainer().clear();
        initCompanies();
    }

    @Override
    public void onMenuClicked( AbstractCompanyItemView itemView ) {
        Company value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new CompanyEvents.ShowPreview( itemView.getPreviewContainer(), value ) );
        animation.showPreview( itemView );
    }

    @Override
    public void onFavoriteClicked( AbstractCompanyItemView itemView ) {
        Window.alert( "Clicked on favorite of company with id = " + itemViewToModel.get( itemView ).getId() + "!" );
    }

    @Override
    public void onCreateClicked() {
        fireEvent( new CompanyEvents.Edit ( null ));
    }

    private void initCompanies() {

        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }
        companyService.getCompanies( view.getSearchPattern(), view.getCategories().getValue(), view.getGroup().getValue(),
                view.getSortField().getValue(), view.getDirSort(), new RequestCallback< List < Company > >() {

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

    private AbstractCompanyItemView makeView( Company company ) {
        AbstractCompanyItemView itemView = factory.get();
        itemView.setActivity( this );
        itemView.setName( company.getCname () );

        CompanyCategory category = company.getCategory();
        if ( category != null ) {
            itemView.setType( category.getName() );
        }
        return itemView;
    }

    @Inject
    Provider< AbstractCompanyItemView > factory;

    @Inject
    AbstractCompanyListView view;

    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;

    @Inject
    PeriodicTaskService taskService;

    @Inject
    PlateListAnimation animation;

    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map< AbstractCompanyItemView, Company > itemViewToModel = new HashMap< AbstractCompanyItemView, Company >();

    private AppEvents.InitDetails initDetails;
}
