package ru.protei.portal.ui.common.client.widget.departmentselector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.service.CompanyDepartmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.departmentselector.item.DepartmentSelectorItem;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public class DepartmentSelector extends BasePopupView implements HasValueChangeHandlers<CompanyDepartment>, HasAddHandlers, HasEditHandlers {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        ensureDebugIds();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CompanyDepartment> handler) {
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
        companyDepartmentController.getCompanyDepartments(companyId, new FluentCallback<List<CompanyDepartment>>()
                .withSuccess(companyDepartments -> {
                    this.companyDepartments = companyDepartments;
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
        boolean isGranted = policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
        clearDepartmentsListView();
        companyDepartments.stream()
                .filter(companyDepartment -> containsIgnoreCase(companyDepartment.getName(), searchNameFilter) || (isGranted ? containsIgnoreCase(companyDepartment.getCompanyId().toString(), searchNameFilter) : false))
                .forEach(this::addDepartmentsToListView);
    }

    private void clearDepartmentsListView() {
        childContainer.clear();
    }

    private void addDepartmentsToListView(CompanyDepartment companyDepartment) {
        DepartmentSelectorItem departmentSelectorItem = departmentSelectorItemProvider.get();
        departmentSelectorItem.setValue(companyDepartment);
        departmentSelectorItem.editIconVisibility().setVisible(true);
        departmentSelectorItem.setEditable(true);
        departmentSelectorItem.addAddHandler(event -> {
            onDepartmentSelected(companyDepartment);
        });
        departmentSelectorItem.addClickHandler(event -> {
            onDepartmentEdit(companyDepartment);
        });
        childContainer.add(departmentSelectorItem);
    }

    private void onDepartmentSelected(CompanyDepartment companyDepartment) {
        ValueChangeEvent.fire(this, companyDepartment);
        hide();
    }

    private void onDepartmentEdit(CompanyDepartment companyDepartment) {
        EditEvent.fire(this, companyDepartment);
        hide();
    }

    private boolean containsIgnoreCase(String test, String sub) {
        if (StringUtils.isBlank(test) || sub == null) {
            return false;
        }
        return test.toLowerCase().contains(sub.toLowerCase());
    }

    @Inject
    CompanyDepartmentControllerAsync companyDepartmentController;
    @Inject
    Provider<DepartmentSelectorItem> departmentSelectorItemProvider;

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
    private List<CompanyDepartment> companyDepartments;
    private boolean enabled;

    interface CompanyDepartmentSelectorPopupUiBinder extends UiBinder<HTMLPanel, DepartmentSelector> {}
    private static CompanyDepartmentSelectorPopupUiBinder ourUiBinder = GWT.create(CompanyDepartmentSelectorPopupUiBinder.class);
}
