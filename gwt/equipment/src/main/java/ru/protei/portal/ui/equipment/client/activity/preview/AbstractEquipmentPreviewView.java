package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractEquipmentPreviewView extends IsWidget {

    void setActivity( AbstractEquipmentPreviewActivity activity );
}
