package ru.protei.portal.ui.equipment.client.view.document.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.equipment.client.activity.document.list.AbstractEquipmentDocumentsListActivity;
import ru.protei.portal.ui.equipment.client.activity.document.list.AbstractEquipmentDocumentsListView;

public class EquipmentDocumentsListView extends Composite implements AbstractEquipmentDocumentsListView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEquipmentDocumentsListActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets kdDocumentsContainer() {
        return kdDocumentsContainer;
    }

    @Override
    public HasWidgets edDocumentsContainer() {
        return edDocumentsContainer;
    }

    @Override
    public HasWidgets tdDocumentsContainer() {
        return tdDocumentsContainer;
    }

    @Override
    public HasWidgets pdDocumentsContainer() {
        return pdDocumentsContainer;
    }

    @UiField
    HTMLPanel kdDocumentsContainer;
    @UiField
    HTMLPanel edDocumentsContainer;
    @UiField
    HTMLPanel tdDocumentsContainer;
    @UiField
    HTMLPanel pdDocumentsContainer;

    private AbstractEquipmentDocumentsListActivity activity;

    interface EquipmentDocumentsListUiBinder extends UiBinder<HTMLPanel, EquipmentDocumentsListView> {}
    private static EquipmentDocumentsListUiBinder ourUiBinder = GWT.create(EquipmentDocumentsListUiBinder.class);
}
