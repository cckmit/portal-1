package ru.protei.portal.ui.equipment.client.activity.document.list.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractEquipmentDocumentsListItemView extends IsWidget {

    void setActivity(AbstractEquipmentDocumentsListItemActivity activity);

    void setApproved(Boolean isApproved);

    void setDecimalNumber(String decimalNumber);

    void setInfo(String info);

    void setEditVisible(boolean visible);
}
