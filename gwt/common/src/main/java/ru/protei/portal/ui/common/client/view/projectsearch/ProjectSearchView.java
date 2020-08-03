package ru.protei.portal.ui.common.client.view.projectsearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.projectsearch.AbstractProjectSearchActivity;
import ru.protei.portal.ui.common.client.activity.projectsearch.AbstractProjectSearchView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.projectlist.list.ProjectList;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;

import java.util.List;
import java.util.Set;

/**
 * Активность поиска проекта
 */
public class ProjectSearchView extends Composite implements AbstractProjectSearchView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        name.getElement().setAttribute("placeholder", lang.inputProjectName());
        products.setState(En_DevUnitState.ACTIVE);
        products.setTypes(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
        dateCreatedRange.setPlaceholder(lang.selectDate());
        customerType.setDefaultValue(lang.selectCustomerType());
    }

    @Override
    public void setActivity( AbstractProjectSearchActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<En_CustomerType> customerType() {
        return customerType;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() { return managers; }

    @Override
    public HasValue<DateInterval> dateCreatedRange() {
        return dateCreatedRange;
    }

    @Override
    public HasValue<ProjectInfo> project() {
        return project;
    }

    @Override
    public void setVisibleProducts(boolean value) {
        productsContainer.setVisible(value);
    }

    @Override
    public void setVisibleManagers(boolean value) {
        managersContainer.setVisible(value);
    }

    @Override
    public void clearProjectList() {
        project.clearItems();
    }

    @Override
    public void fillProjectList(List<ProjectInfo> list) {
        searchInfo.removeClassName("hide");
        projectsContainer.removeClassName("hide");
        project.addItems(list);
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        customerType.setValue(null);
        products.setValue(null);
        managers.setValue(null);
        dateCreatedRange.setValue(null);
        searchInfo.addClassName("hide");
        projectsContainer.addClassName("hide");
        project.clearItems();
    }

    @Override
    public void setDialogViewStyles() {
        nameContainer.removeStyleName("col-md-4");
        nameContainer.addStyleName("col-md-12");

        dateContainer.removeStyleName("col-md-4");
        dateContainer.addStyleName("col-md-12");

        customerTypeContainer.removeStyleName("col-md-4");
        customerTypeContainer.addStyleName("col-md-12");
    }

    @Override
    public void setWideFormStyles() {
        nameContainer.removeStyleName("col-md-12");
        nameContainer.addStyleName("col-md-4");

        dateContainer.removeStyleName("col-md-12");
        dateContainer.addStyleName("col-md-4");

        customerTypeContainer.removeStyleName("col-md-12");
        customerTypeContainer.addStyleName("col-md-4");
    }

    @UiHandler("search")
    public void onSearchClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onSearchClicked();
        }
    }

    @UiHandler("reset")
    public void onResetClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onResetClicked();
        }
    }

    @UiHandler("project")
    public void onProjectSelected(ValueChangeEvent<ProjectInfo> event) {
        if (activity != null) {
            activity.onProjectSelected();
        }
    }

    private void ensureDebugIds() {
        name.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.NAME_INPUT);
        dateCreatedRange.setEnsureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.CREATION_DATE_INPUT);
        dateCreatedRange.getRelative().ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.CREATION_DATE_BUTTON);
        customerType.setEnsureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.CUSTOMER_TYPE_SELECTOR);
        products.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.PRODUCT_SELECTOR);
        productsContainer.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.PRODUCTS_CONTAINER);
        managers.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.MANAGER_SELECTOR);
        managersContainer.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.MANAGERS_CONTAINER);
        search.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.FIND_BUTTON);
        reset.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.RESET_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_SEARCH.NAME_LABEL);
        dateCreatedRangeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_SEARCH.CREATION_DATE_LABEL);
        customerTypeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_SEARCH.CUSTOMER_TYPE_LABEL);
        productsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_SEARCH.PRODUCT_LABEL);
        searchInfo.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_SEARCH.SHOW_FIRST_RECORDS_LABEL);
    }

    @UiField
    TextBox name;

    @UiField
    HTMLPanel nameContainer;

    @Inject
    @UiField(provided = true)
    CustomerTypeSelector customerType;

    @UiField
    HTMLPanel customerTypeContainer;

    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector products;

    @UiField
    HTMLPanel productsContainer;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector managers;

    @UiField
    HTMLPanel managersContainer;

    @Inject
    @UiField(provided = true)
    RangePicker dateCreatedRange;

    @UiField
    HTMLPanel dateContainer;

    @UiField
    Anchor search;

    @UiField
    Anchor reset;

    @Inject
    @UiField(provided = true)
    ProjectList project;

    @UiField
    DivElement projectsContainer;

    @UiField
    Element searchInfo;

    @UiField
    LabelElement nameLabel;

    @UiField
    LabelElement dateCreatedRangeLabel;

    @UiField
    LabelElement customerTypeLabel;

    @UiField
    LabelElement productsLabel;

    @Inject
    @UiField
    Lang lang;

    private AbstractProjectSearchActivity activity;

    private static ProjectSearchViewUiBinder ourUiBinder = GWT.create(ProjectSearchViewUiBinder.class);
    interface ProjectSearchViewUiBinder extends UiBinder<HTMLPanel, ProjectSearchView > {}
}