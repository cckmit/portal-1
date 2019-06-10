package ru.protei.portal.ui.document.client.activity.search;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SearchProjectActivity implements Activity, AbstractSearchProjectActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        dialogView.setActivity( this );
        dialogView.setHeader( lang.documentSearchProject() );
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

    @Override
    public void onSaveClicked() {

    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    @Override
    public void onSearchClicked() {
        fireEvent(new ProjectEvents.ShowDetailedTable(view.getProjectContainer(), makeQuery()));
    }

    private ProjectQuery makeQuery() {
        ProjectQuery query = new ProjectQuery(view.name().getValue(), En_SortField.creation_date, En_SortDir.DESC);
        DateInterval createdInterval = view.dateCreatedRange().getValue();
        if (createdInterval != null) {
            query.setCreatedFrom(createdInterval.from);
            query.setCreatedTo(createdInterval.to);
        }
        query.setCustomerType(view.customerType().getValue());
        query.setProductIds(view.products().getValue().stream().map(product -> product.getId()).collect( Collectors.toList()));
        return query;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractSearchProjectView view;
    @Inject
    AbstractDialogDetailsView dialogView;
}
