package ru.protei.portal.ui.equipment.client.view.copy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.equipment.client.activity.copy.AbstractEquipmentCopyActivity;
import ru.protei.portal.ui.equipment.client.activity.copy.AbstractEquipmentCopyView;


/**
 * Вид диалога копирования оборудования
 */
public class EquipmentCopyView extends Composite implements AbstractEquipmentCopyView {

    public EquipmentCopyView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEquipmentCopyActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @UiField
    TextBox name;

    AbstractEquipmentCopyActivity activity;

    interface ServiceViewUiBinder extends UiBinder<Widget, EquipmentCopyView> {}
    private static ServiceViewUiBinder ourUiBinder = GWT.create(ServiceViewUiBinder.class);

}