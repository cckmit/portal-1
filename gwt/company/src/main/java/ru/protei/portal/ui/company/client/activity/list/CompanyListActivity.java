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
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;

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

        view.getCompanyContainer ().clear ();

        for (Company c : getCompanies ()) {
            AbstractCompanyItemView itemView = factory.get ();
            itemView.setActivity (this);
            itemView.setName (c.getCname ());
            itemView.setType (lang.customers ());
            map.put (c, itemView);
            view.getCompanyContainer ().add (itemView.asWidget ());
        }

        this.fireEvent (new AppEvents.InitPanelName (lang.companies()));
        initDetails.parent.clear ();
        initDetails.parent.add (view.asWidget ());
    }

    private List<Company> getCompanies() {
        List<Company> companies = new ArrayList<Company> ();
        Company company = new Company ();
        company.setCname ("Мегафон");
        companies.add (company);
        company = new Company ();
        company.setCname ("Ростелеком");
        companies.add (company);
        company = new Company ();
        company.setCname ("АРКТЕЛ");
        companies.add (company);
        company = new Company ();
        company.setCname ("Alepo - EAPSIM");
        companies.add (company);
        return companies;
    }
    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Inject
    Provider<AbstractCompanyItemView> factory;

    Map<Company, AbstractCompanyItemView> map = new HashMap<Company, AbstractCompanyItemView> ();

    @Inject
    AbstractCompanyListView view;

    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
}
