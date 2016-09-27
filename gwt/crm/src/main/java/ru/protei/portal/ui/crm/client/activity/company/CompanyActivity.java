package ru.protei.portal.ui.crm.client.activity.company;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.crm.client.events.AppEvents;
import ru.protei.portal.ui.crm.client.events.CompanyEvents;

/**
 * Created by turik on 27.09.16.
 */
public abstract class CompanyActivity implements AbstractCompanyActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity (this);
    }

    @Event
    public void onShow (CompanyEvents.Show event) {
        this.fireEvent (new AppEvents.InitPanelName ("Companies"));
        parent.add (view.asWidget ());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.parent = event.parent;
        this.fireEvent (new CompanyEvents.Show());
    }

    @Inject
    AbstractCompanyView view;

    public HasWidgets parent;
}
