package ru.protei.portal.ui.documentation.client.activity.table;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;


public abstract class DocumentationTableActivity
        implements Activity, AbstractDocumentationTableActivity, AbstractPagerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setAnimation(animation);

        CREATE_ACTION = lang.buttonCreate();

        pagerView.setPageSize(view.getPageSize());
        pagerView.setActivity(this);

    }

    @Event
    public void onShow(DocumentationEvents.Show event) {
        fireEvent(new AppEvents.InitPanelName(lang.documentation()));

        init.parent.clear();
        init.parent.add(view.asWidget());
        init.parent.add(pagerView.asWidget());

        fireEvent(new ActionBarEvents.Add(CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.DOCUMENTATION));
    }

    @Override
    public void onFirstClicked() {
        GWT.log("first clicked");
    }

    @Override
    public void onLastClicked() {
        GWT.log("last clicked");
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.init = initDetails;
    }

    @Override
    public void onItemClicked(Documentation value) {
        GWT.log(this.getClass().getName()  + ": item clicked");
    }

    @Override
    public void onEditClicked(Documentation value) {
        fireEvent(new DocumentationEvents.Edit(value.getId()));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentationTableView view;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    TableAnimation animation;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails init;
}
