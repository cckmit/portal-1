package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

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
            fireEvent(new AppEvents.InitPanelName(lang.newIssue()));
        }else {
            fireEvent(new AppEvents.InitPanelName(lang.issueEdit()));
        }
    }

    @Override
    public void onSaveClicked() {

    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }



    @Inject
    AbstractIssueEditView view;
    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;


}
