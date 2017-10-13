package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстрактное представление должностного лица
 */
public interface AbstractOfficialItemView extends IsWidget {

    void setName(String firstName);

    void setAmplua(String amplua);

    void setPosition(String position);

    void setRelations(String relations);

    void setActivity(AbstractOfficialItemActivity activity);

    void setButtonsVisibility(boolean isVisible);

    void setComments(String comments);
}