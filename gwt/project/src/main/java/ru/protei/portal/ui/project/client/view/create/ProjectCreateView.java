package ru.protei.portal.ui.project.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.project.client.activity.create.AbstractProjectCreateActivity;
import ru.protei.portal.ui.project.client.activity.create.AbstractProjectCreateView;

import java.util.Set;

public class ProjectCreateView extends Composite implements AbstractProjectCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractProjectCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> description() {
        return description;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
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
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasWidgets createProductContainer() {
        return createProductContainer;
    }

    @UiHandler("saveBtn")
    public void onSaveClicked(ClickEvent event)
    {
        if (activity != null)
            activity.onSaveClicked();
    }

    @UiHandler("cancelBtn")
    public void onCancelClicked(ClickEvent event)
    {
        if (activity != null)
            activity.onCancelClicked();
    }

    @UiHandler("createProductBtn")
    public void onCreateProductClicked(ClickEvent event)
    {
        if (activity != null)
            activity.onCreateProductClicked();
    }

    @UiField
    ValidableTextBox name;
    @UiField
    TextArea description;
    @Inject
    @UiField(provided = true)
    CompanySelector company;
    @Inject
    @UiField(provided = true)
    CustomerTypeSelector customerType;
    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector products;
    @UiField
    Button saveBtn;
    @UiField
    Button cancelBtn;
    @UiField
    HTMLPanel createProductContainer;
    @UiField
    Button createProductBtn;

    AbstractProjectCreateActivity activity;

    private static ProjectCreateViewUiBinder ourUiBinder = GWT.create(ProjectCreateViewUiBinder.class);
    interface ProjectCreateViewUiBinder extends UiBinder<HTMLPanel, ProjectCreateView> {}
}