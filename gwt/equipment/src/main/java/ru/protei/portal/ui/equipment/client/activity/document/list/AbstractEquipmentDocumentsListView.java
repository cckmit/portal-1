package ru.protei.portal.ui.equipment.client.activity.document.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractEquipmentDocumentsListView extends IsWidget {

    void setActivity(AbstractEquipmentDocumentsListActivity activity);

    HasWidgets kdDocumentsContainer();

    HasWidgets edDocumentsContainer();

    HasWidgets tdDocumentsContainer();

    HasWidgets pdDocumentsContainer();
}
