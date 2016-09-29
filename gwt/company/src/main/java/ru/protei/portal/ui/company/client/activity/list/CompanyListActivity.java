package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Created by turik on 27.09.16.
 */
public abstract class CompanyListActivity implements AbstractCompanyListActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity (this);
    }

    @Event
    public void onShow (CompanyEvents.Show event) {
        this.fireEvent (new AppEvents.InitPanelName (lang.companies()));
        parent.add (view.asWidget ());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.parent = event.parent;
    }

    @Inject
    AbstractCompanyListView view;
    @Inject
    Lang lang;

    public HasWidgets parent;
}
