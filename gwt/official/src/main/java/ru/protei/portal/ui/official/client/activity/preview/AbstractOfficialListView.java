package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстрактное представление списка должностных лиц
 */
public interface AbstractOfficialListView extends IsWidget{

    void setCompanyName(String key);

    HTMLPanel getItemContainer();
}
