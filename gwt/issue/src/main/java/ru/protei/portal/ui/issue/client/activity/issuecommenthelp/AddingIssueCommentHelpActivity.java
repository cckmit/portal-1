package ru.protei.portal.ui.issue.client.activity.issuecommenthelp;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.service.AppServiceAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issue.client.activity.item.AbstractAddingIssueCommentHelpItemView;

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
        view.textContainer().clear();

        init.parent.clear();
        init.parent.add(view.asWidget());

        fillView();
    }

    private void fillView() {
        String fileName = "help_" + LocaleInfo.getCurrentLocale().getLocaleName() + ".html";
        appService.getIssueCommentHelpHtml(fileName, new FluentCallback<String>()
                  .withSuccess(html -> {
                    AbstractAddingIssueCommentHelpItemView itemView = provider.get();
                    itemView.setHelpText(html);
                    view.textContainer().add(itemView.asWidget());
                    itemView.addRootStyle("col-md-12 issue_comment_help");
                }));
    }

    @Inject
    AbstractAddingIssueCommentHelpView view;

    @Inject
    Provider<AbstractAddingIssueCommentHelpItemView> provider;

    @Inject
    AppServiceAsync appService;

    private AppEvents.InitDetails init;
}
