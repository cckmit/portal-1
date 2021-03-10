package ru.protei.portal.ui.issue.client.activity.issuecommenthelp;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.service.AppServiceAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class AddingIssueCommentHelpActivity implements Activity, AbstractAddingIssueCommentHelpActivity {

    @Inject
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Event
    public void onAddingCommentShow(IssueEvents.ShowAddingIssueCommentHelp event) {
        fireEvent(new ActionBarEvents.Clear());
        view.helpTextContainer().setInnerHTML("");

        init.parent.clear();
        init.parent.add(view.asWidget());

        fillView();
    }

    private void fillView() {
        appService.getAddingIssueCommentHelpText(
                LocaleInfo.getCurrentLocale().getLocaleName(),
                new FluentCallback<String>().withSuccess(text -> view.helpTextContainer().setInnerHTML(text)));
    }

    @Inject
    AbstractAddingIssueCommentHelpView view;

    @Inject
    AppServiceAsync appService;

    private AppEvents.InitDetails init;
}
