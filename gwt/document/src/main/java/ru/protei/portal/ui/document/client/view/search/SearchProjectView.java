package ru.protei.portal.ui.document.client.view.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.document.client.activity.search.AbstractSearchProjectActivity;
import ru.protei.portal.ui.document.client.activity.search.AbstractSearchProjectView;
import ru.protei.portal.ui.document.client.widget.projectlist.list.ProjectList;

import java.util.List;
import java.util.Set;

public class SearchProjectView extends Composite implements AbstractSearchProjectView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dateCreatedRange.setPlaceholder(lang.selectDate());
    }

    @Override
    public void setActivity(AbstractSearchProjectActivity activity) {
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
    public HasValue<ProjectInfo> project() {
        return project;
    }

    @Override
    public void clearProjectList() {
        project.clearItems();
    }

    @Override
    public void fillProjectList(List<ProjectInfo> list) {
        panel.setVisible(true);
        project.addItems(list);
    }

    @Override
    public HasWidgets createProjectContainer() {
        return createProjectContainer;
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        customerType.setValue(null);
        products.setValue(null);
        dateCreatedRange.setValue(null);
        panel.setVisible(false);
        project.clearItems();
    }

    @UiHandler("search")
    public void onSearchClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onSearchClicked();
        }
    }

    @UiHandler("clear")
    public void onClearClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onClearClicked();
        }
    }

    @UiHandler("create")
    public void onCreateClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onCreateProjectClicked();
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
    Anchor clear;

    @Inject
    @UiField(provided = true)
    ProjectList project;

    @UiField
    HTMLPanel panel;

    @UiField
    Anchor create;

    @UiField
    HTMLPanel createProjectContainer;

    @Inject
    @UiField
    Lang lang;

    private AbstractSearchProjectActivity activity;

    private static SearchProjectViewUiBinder ourUiBinder = GWT.create( SearchProjectViewUiBinder.class );
    interface SearchProjectViewUiBinder extends UiBinder< HTMLPanel, SearchProjectView > {}
}