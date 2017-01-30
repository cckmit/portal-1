package ru.protei.portal.ui.equipment.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.equipment.client.activity.preview.AbstractEquipmentPreviewActivity;
import ru.protei.portal.ui.equipment.client.activity.preview.AbstractEquipmentPreviewView;

/**
 * Вид превью контакта
 */
public class EquipmentPreviewView extends Composite implements AbstractEquipmentPreviewView {

    public EquipmentPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity( AbstractEquipmentPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

   
    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    AbstractEquipmentPreviewActivity activity;

    interface EquipmentPreviewViewUiBinder extends UiBinder<HTMLPanel, EquipmentPreviewView > { }
    private static EquipmentPreviewViewUiBinder ourUiBinder = GWT.create(EquipmentPreviewViewUiBinder.class);
}