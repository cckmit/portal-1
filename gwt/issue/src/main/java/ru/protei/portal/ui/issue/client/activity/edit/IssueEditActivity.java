package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( IssueEvents.Edit event ) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());


        if(event.issueId == null) {
            fireEvent(new AppEvents.InitPanelName("new"));
        }else {
            fireEvent(new AppEvents.InitPanelName("edit"));
        }
    }

    @Inject
    AbstractIssueEditiew view;

    private AppEvents.InitDetails initDetails;

}
