package ru.protei.portal.ui.official.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionButtonSelector;
import ru.protei.portal.ui.official.client.activity.edit.AbstractOfficialEditActivity;
import ru.protei.portal.ui.official.client.activity.edit.AbstractOfficialEditView;

/**
 * Created by serebryakov on 31/08/17.
 */
public class OfficialEditView extends Composite implements AbstractOfficialEditView{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        region.setDefaultValue(lang.selectOfficialRegion());
        product.setDefaultValue(lang.selectOfficialProduct());
    }

    @Override
    public void setActivity(AbstractOfficialEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<EntityOption> region() {
        return region;
    }

    @Override
    public HasValue<ProductShortView> product() {
        return product;
    }

    @Override
    public HasValue<String> info() {
        return info;
    }

    @UiHandler("saveButton")
    public void onSaveButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    RegionButtonSelector region;

    @Inject
    @UiField(provided = true)
    ProductButtonSelector product;

    @UiField
    TextArea info;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private AbstractOfficialEditActivity activity;

    private static OfficialEditViewUiBinder ourUiBinder = GWT.create(OfficialEditViewUiBinder.class);

    interface OfficialEditViewUiBinder extends UiBinder<HTMLPanel, OfficialEditView> {}
}