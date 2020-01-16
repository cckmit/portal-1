package ru.protei.portal.ui.project.client.view.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
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
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.project.client.activity.search.AbstractProjectSearchActivity;
import ru.protei.portal.ui.project.client.activity.search.AbstractProjectSearchView;
import ru.protei.portal.ui.project.client.view.widget.projectlist.list.ProjectList;

import java.util.List;
import java.util.Set;

/**
 * Активность поиска проекта
 */
public class ProjectSearchView extends Composite implements AbstractProjectSearchView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        name.getElement().setAttribute("placeholder", lang.inputProjectName());
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
    public HasValue<DateInterval> dateCreatedRange() {
        return dateCreatedRange;
    }

    @Override
    public HasValue<Project> project() {
        return project;
    }

    @Override
    public void clearProjectList() {
        project.clearItems();
    }

    @Override
    public void fillProjectList(List<Project> list) {
        searchInfo.removeClassName("hide");
        projectsContainer.removeClassName("hide");
        project.addItems(list);
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        customerType.setValue(null);
        products.setValue(null);
        dateCreatedRange.setValue(null);
        searchInfo.addClassName("hide");
        projectsContainer.addClassName("hide");
        project.clearItems();
    }

//    @Override
//    public void refreshProducts() {
//        products.refreshOptions();
//    }

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
    public void onProjectChanged(ValueChangeEvent<Project> event) {
        if (activity != null) {
            activity.onProjectChanged();
        }
    }

    @UiField
    TextBox name;

    @Inject
    @UiField(provided = true)
    CustomerTypeSelector customerType;

    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector products;

    @Inject
    @UiField(provided = true)
    RangePicker dateCreatedRange;

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

    @Inject
    @UiField
    Lang lang;

    private AbstractProjectSearchActivity activity;

    private static SearchProjectViewUiBinder ourUiBinder = GWT.create(SearchProjectViewUiBinder.class);
    interface SearchProjectViewUiBinder extends UiBinder<HTMLPanel, ProjectSearchView > {}
}