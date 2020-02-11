package ru.protei.portal.ui.project.client.view.quickcreate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.project.client.activity.quickcreate.AbstractProjectCreateActivity;
import ru.protei.portal.ui.project.client.activity.quickcreate.AbstractProjectCreateView;

/**
 * Представление создания проекта с минимальным набором параметров
 */
public class ProjectCreateView extends Composite implements AbstractProjectCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugId();
        product.updateQuery(En_DevUnitState.ACTIVE, En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
    }

    @Override
    public void setActivity(AbstractProjectCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public void updateProductDirection(Long directionId) {
        product.updateDirection(directionId);
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
    public HasValue<EntityOption> region() { return region; }

    @Override
    public HasValue<ProductDirectionInfo> direction() { return direction; }

    @Override
    public HasValue<En_CustomerType> customerType() {
        return customerType;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<ProductShortView> product() {
        return product;
    }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasValidable regionValidator() {
        return region;
    }

    @Override
    public HasValidable directionValidator() {
        return direction;
    }

    @Override
    public HasValidable customerTypeValidator() {
        return customerType;
    }

    @Override
    public HasValidable companyValidator() {
        return company;
    }

//    @Override
//    public void refreshProducts() {
//        product.refreshOptions();
//    }

    @UiHandler("saveBtn")
    public void onSaveClicked(ClickEvent event)
    {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked(ClickEvent event)
    {
        if (activity != null) {
            activity.onResetClicked();
        }
    }

    @UiHandler("direction")
    public void onDirectionChanged(ValueChangeEvent<ProductDirectionInfo> event) {
        if (activity != null) {
            activity.onDirectionChanged();
        }
    }

    private void ensureDebugId() {
        name.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.NAME_INPUT);
        description.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.DESCRIPTION_INPUT);
        region.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.REGION_SELECTOR);
        direction.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.DIRECTION_SELECTOR);
        customerType.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.CUSTOMER_TYPE_SELECTOR);
        company.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.COMPANY_SELECTOR);
        product.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.PRODUCT_SELECTOR);
        saveBtn.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.SAVE_BUTTON);
        resetBtn.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.RESET_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_CREATE.NAME_LABEL);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_CREATE.DESCRIPTION_LABEL);
        regionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_CREATE.REGION_LABEL);
        directionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_CREATE.DIRECTION_LABEL);
        customerTypeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_CREATE.CUSTOMER_TYPE_LABEL);
        companyLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_CREATE.COMPANY_LABEL);
        productLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PROJECT_CREATE.PRODUCT_LABEL);
    }

    @UiField
    ValidableTextBox name;

    @UiField
    TextArea description;

    @Inject
    @UiField(provided = true)
    RegionButtonSelector region;

    @Inject
    @UiField(provided = true)
    ProductDirectionButtonSelector direction;

    @Inject
    @UiField(provided = true)
    CustomerTypeSelector customerType;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    DevUnitButtonSelector product;

    @UiField
    Button saveBtn;

    @UiField
    Button resetBtn;

    @UiField
    LabelElement nameLabel;

    @UiField
    LabelElement descriptionLabel;

    @UiField
    LabelElement regionLabel;

    @UiField
    LabelElement directionLabel;

    @UiField
    LabelElement customerTypeLabel;

    @UiField
    LabelElement companyLabel;

    @UiField
    LabelElement productLabel;

    @Inject
    @UiField
    Lang lang;

    AbstractProjectCreateActivity activity;

    private static ProjectCreateViewUiBinder ourUiBinder = GWT.create(ProjectCreateViewUiBinder.class);
    interface ProjectCreateViewUiBinder extends UiBinder<HTMLPanel, ProjectCreateView> {}
}