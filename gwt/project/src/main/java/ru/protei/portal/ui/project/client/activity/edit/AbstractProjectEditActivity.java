package ru.protei.portal.ui.project.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция активности карточки создания/редактирования проекта
 */
public interface AbstractProjectEditActivity{
    void onSaveClicked();
    void onCancelClicked();
    void onAddLinkClicked(IsWidget anchor);
    void onDirectionChanged();
}