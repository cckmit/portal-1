package ru.protei.portal.ui.document.client.activity.search;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class SearchProjectActivity implements Activity, AbstractSearchProjectActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        dialogView.setActivity( this );
        dialogView.setHeader( lang.issueReportNew() );
        dialogView.getBodyContainer().add( view.asWidget() );
    }

    @Event
    public void onShow( ProjectEvents.Search event ) {
/*
        view.resetFilter();
        resetFilters();
        applyFilterViewPrivileges();
*/
        dialogView.showPopup();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractSearchProjectView view;
    @Inject
    AbstractDialogDetailsView dialogView;
}
