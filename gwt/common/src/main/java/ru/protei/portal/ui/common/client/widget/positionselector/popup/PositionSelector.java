package ru.protei.portal.ui.common.client.widget.positionselector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.WorkerPosition;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.service.WorkerPositionControllerAsync;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.positionselector.item.PositionSelectorItem;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public class PositionSelector extends BasePopupView implements HasValueChangeHandlers<WorkerPosition>, HasAddHandlers, HasEditHandlers {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        ensureDebugIds();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<WorkerPosition> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @Override
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
    }

    @Override
    protected UIObject getPositionRoot() {
        return root;
    }

    public void setCompanyId(Long companyId) {
        resetSearchFilter();
        workerPositionController.getWorkerPositions(companyId, new FluentCallback<List<WorkerPosition>>()
                .withSuccess(workerPositions -> {
                    this.workerPositions = workerPositions;
                    displayDepartments();
                })
        );
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        searchNameFilter = event.getValue();
        displayDepartments();
    }

    @UiHandler("addButton")
    public void addButtonClick(ClickEvent event) {
        AddEvent.fire(this);
        hide();
    }

    public void setAddDepartmentEnabled(boolean enabled) {
        if (enabled) {
            addButton.getElement().removeClassName("hide");
        } else {
            addButton.getElement().addClassName("hide");
        }
    }

    public void setEditDepartmentsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void ensureDebugIds() {
        //addButton.ensureDebugId(DebugIds.ISSUE.ADD_TAG_BUTTON);
    }

    private void resetSearchFilter() {
        searchNameFilter = "";
        search.setValue(searchNameFilter);
        search.setFocus(true);
    }

    private void displayDepartments() {
       // boolean isGranted = policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
        clearDepartmentsListView();
        workerPositions.stream()
                .filter(workerPosition -> containsIgnoreCase(workerPosition.getName(), searchNameFilter))
                .forEach(this::addDepartmentsToListView);
    }

    private void clearDepartmentsListView() {
        childContainer.clear();
    }

    private void addDepartmentsToListView(WorkerPosition workerPosition) {
        PositionSelectorItem positionSelectorItem = positionSelectorItemProvider.get();
        positionSelectorItem.setValue(workerPosition);
        positionSelectorItem.editIconVisibility().setVisible(true);
        positionSelectorItem.setEditable(true);
        positionSelectorItem.addAddHandler(event -> {
            onDepartmentSelected(workerPosition);
        });
        positionSelectorItem.addClickHandler(event -> {
            onDepartmentEdit(workerPosition);
        });
        childContainer.add(positionSelectorItem);
    }

    private void onDepartmentSelected(WorkerPosition workerPosition) {
        ValueChangeEvent.fire(this, workerPosition);
        hide();
    }

    private void onDepartmentEdit(WorkerPosition workerPosition) {
        EditEvent.fire(this, workerPosition);
        hide();
    }

    private boolean containsIgnoreCase(String test, String sub) {
        if (StringUtils.isBlank(test) || sub == null) {
            return false;
        }
        return test.toLowerCase().contains(sub.toLowerCase());
    }

    @Inject
    WorkerPositionControllerAsync workerPositionController;
    @Inject
    Provider<PositionSelectorItem> positionSelectorItemProvider;

    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField
    CleanableSearchBox search;
    @UiField
    Button addButton;
    @UiField
    HTMLPanel childContainer;

    @Inject
    PolicyService policyService;

    private String searchNameFilter = "";
    private List<WorkerPosition> workerPositions;
    private boolean enabled;

    interface WorkerPositionSelectorPopupUiBinder extends UiBinder<HTMLPanel, PositionSelector> {}
    private static WorkerPositionSelectorPopupUiBinder ourUiBinder = GWT.create(WorkerPositionSelectorPopupUiBinder.class);
}
