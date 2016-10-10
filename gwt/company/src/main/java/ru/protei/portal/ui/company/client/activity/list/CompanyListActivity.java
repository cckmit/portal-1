package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Активность списка компаний
 */
public abstract class CompanyListActivity implements AbstractCompanyListActivity, AbstractCompanyItemActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( CompanyEvents.Show event ) {

        this.fireEvent( new AppEvents.InitPanelName( lang.companies() ) );
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        view.getGroupList().clear();
        initGroups();

        view.getCompanyContainer().clear();
        initCompanies();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    public void onSearchClicked() {

        view.getCompanyContainer().clear();
        initCompanies();
    }

    @Override
    public void onMenuClicked( AbstractCompanyItemView itemView ) {

        Window.alert( "Clicked on menu of company with id = " + map.get( itemView ).getId() + "!" );
    }

    @Override
    public void onFavoriteClicked( AbstractCompanyItemView itemView ) {

        Window.alert( "Clicked on favorite of company with id = " + map.get( itemView ).getId() + "!" );
    }

    private void initCompanies() {

        list.clear();
        companyService.getCompanies( view.getSearchPattern(), new RequestCallback<List<Company>>() {

            @Override
            public void onError( Throwable throwable ) {
            }

            @Override
            public void onSuccess( List<Company> companies ) {
                list.addAll( companies );
                fillView();
            }
        });
    }

    private void fillView() {

        //int recNum = 1;
        for ( Company company : list ) {
            AbstractCompanyItemView itemView = makeView( company );

            map.put( itemView, company );
            view.getCompanyContainer().add( itemView.asWidget() );

            /*if ( ++recNum > 500 ) {
                break;
            }*/
        }
    }

    private AbstractCompanyItemView makeView( Company company ) {

        AbstractCompanyItemView itemView = factory.get();
        itemView.setActivity( this );
        itemView.setName( company.getCname () );
        itemView.setType( lang.customer () );
        return itemView;
    }

    private void initGroups() {
        view.getGroupList().addItem("Группа компаний", "-1");
        view.getGroupList().addItem("Мегафон", "1");
        view.getGroupList().addItem("DPI_Мегафон", "2");
        view.getGroupList().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                view.getCompanyContainer().clear();
                initCompanies();
            }
        });
    }

    @Inject
    Provider<AbstractCompanyItemView> factory;

    @Inject
    AbstractCompanyListView view;

    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;

    private List<Company> list = new ArrayList<Company>();

    private Map<AbstractCompanyItemView, Company> map = new HashMap<AbstractCompanyItemView, Company>();

    private AppEvents.InitDetails initDetails;
}
