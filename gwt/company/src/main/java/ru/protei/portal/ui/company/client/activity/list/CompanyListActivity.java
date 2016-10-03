package ru.protei.portal.ui.company.client.activity.list;

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
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.portal.ui.company.client.service.CompanyServiceAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Активность списка компаний
 */
public abstract class CompanyListActivity implements AbstractCompanyListActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity (this);
    }

    @Event
    public void onShow (CompanyEvents.Show event) {

        this.fireEvent (new AppEvents.InitPanelName (lang.companies ()));
        initDetails.parent.clear ();
        initDetails.parent.add (view.asWidget ());

        view.getCompanyContainer ().clear ();
        initCompanies ();
    }

    private void initCompanies() {

        list.clear ();
        companyService.getCompanies (view.getSearchPattern (), new RequestCallback<List<Company>> () {

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onSuccess(List<Company> companies) {
                list.addAll (companies);
                fillView ();
            }
        });
    }

    private void fillView () {

        int count = list.size ();
        if (count > 500)
            count = 500;

        for (int i=0; i<count; i++) {
            AbstractCompanyItemView itemView = factory.get ();
            itemView.setActivity (this);
            itemView.setName (list.get (i).getCname ());
            itemView.setType (lang.customer ());
            map.put (list.get (i), itemView);
            view.getCompanyContainer ().add (itemView.asWidget ());
        }
    }

    public void onSearchClicked() {

        view.getCompanyContainer ().clear ();
        initCompanies ();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Inject
    Provider<AbstractCompanyItemView> factory;

    @Inject
    AbstractCompanyListView view;

    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;

    private List<Company> list = new ArrayList<Company> ();

    private Map<Company, AbstractCompanyItemView> map = new HashMap<Company, AbstractCompanyItemView> ();

    private AppEvents.InitDetails initDetails;
}
