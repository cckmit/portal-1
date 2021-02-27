package ru.protei.portal.ui.issue.client.activity.issuecommenthelp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
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
        AbstractAddingIssueCommentHelpItemView itemView = makeItem();
        view.textContainer().add(itemView.asWidget());
        itemView.addRootStyle("col-md-12");
    }

    private AbstractAddingIssueCommentHelpItemView makeItem() {
        AbstractAddingIssueCommentHelpItemView itemView = provider.get();
        itemView.setHeader(lang.addingIssueCommentHelp());
        return itemView;
    }

    @Inject
    AbstractAddingIssueCommentHelpView view;

    @Inject
    Provider<AbstractAddingIssueCommentHelpItemView> provider;

    private AppEvents.InitDetails init;

    @Inject
    Lang lang;
}
