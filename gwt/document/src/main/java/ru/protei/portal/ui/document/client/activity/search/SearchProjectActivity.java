package ru.protei.portal.ui.document.client.activity.search;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.document.client.widget.projectlist.item.ProjectItem;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SearchProjectActivity implements Activity, AbstractSearchProjectActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.setHeader(lang.documentSearchProject());
        dialogView.addStyleName("modal-lg");
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(ProjectEvents.Search event) {
        view.resetFilter();
        dialogView.showPopup();
    }

    @Event
    public void onCreatedProject(ProjectEvents.Created event) {
        onSearchClicked();
        view.createProjectContainer().clear();
    }

    @Event
    public void onCanceledCreationProject(ProjectEvents.Canceled event) {
        view.createProjectContainer().clear();
    }

    @Override
    public void onSearchClicked() {
        ProjectQuery query = makeQuery();
        if (isQueryNotValid(query)) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
        } else {
            view.clearProjectList();
            regionService.getProjectsList(query, new FluentCallback<List<ProjectInfo>>()
                    .withErrorMessage(lang.errGetList())
                    .withSuccess(result -> {
                        view.fillProjectList(result);
                    }));
        }
    }

    @Override
    public void onClearClicked() {
        view.resetFilter();
    }

    @Override
    public void onCreateProjectClicked() {
        fireEvent(new ProjectEvents.Create(view.createProjectContainer()));
    }

    @Override
    public void onSaveClicked() {
        dialogView.hidePopup();
        ProjectInfo project = view.project().getValue();
        if (project != null) {
            fireEvent(new ProjectEvents.Set(view.project().getValue()));
        }
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
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

    private boolean isQueryNotValid(ProjectQuery query) {
        return query == null || !query.isParamsPresent();
    }

    @Inject
    RegionControllerAsync regionService;

    @Inject
    Lang lang;

    @Inject
    AbstractSearchProjectView view;

    @Inject
    AbstractDialogDetailsView dialogView;
}
