package ru.protei.portal.ui.equipment.client.view.document.list.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.equipment.client.activity.document.list.item.AbstractEquipmentDocumentsListItemActivity;
import ru.protei.portal.ui.equipment.client.activity.document.list.item.AbstractEquipmentDocumentsListItemView;

public class EquipmentDocumentsListItemView extends Composite implements AbstractEquipmentDocumentsListItemView {

    private final static String ICON_APPROVED = "fa-check-circle-o";
    private final static String ICON_NOT_APPROVED = "fa-file-text-o";

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEquipmentDocumentsListItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setApproved(Boolean isApproved) {
        approve.addClassName(isApproved ? ICON_APPROVED : ICON_NOT_APPROVED);
    }

    @Override
    public void setDecimalNumber(String number) {
        if (StringUtils.isNotBlank(number)) {
            decimalNumber.setInnerText(number.trim());
            decimalNumber.removeClassName("hide");
        } else {
            decimalNumber.addClassName("hide");
        }
    }

    @Override
    public void setInfo(String info) {
        if (StringUtils.isNotBlank(info)) {
            information.setInnerText(info.trim());
            information.removeClassName("hide");
        } else {
            information.addClassName("hide");
        }
    }

    @Override
    public void setEditVisible(boolean visible) {
        edit.setVisible(visible);
    }

    @UiHandler("edit")
    public void editClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onEditClicked(this);
        }
    }

    @UiHandler("download")
    public void copyClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onDownloadClicked(this);
        }
    }

    @UiField
    Element approve;
    @UiField
    SpanElement decimalNumber;
    @UiField
    SpanElement information;
    @UiField
    Anchor edit;
    @UiField
    Anchor download;

    private AbstractEquipmentDocumentsListItemActivity activity;

    interface EquipmentDocumentsListItemUiBinder extends UiBinder<HTMLPanel, EquipmentDocumentsListItemView> {}
    private static EquipmentDocumentsListItemUiBinder ourUiBinder = GWT.create(EquipmentDocumentsListItemUiBinder.class);
}
