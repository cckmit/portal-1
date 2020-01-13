package ru.protei.portal.ui.common.client.activity.loading;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Вид виджета загрузки
 */
public interface AbstractLoadingView extends IsWidget {
    void setActivity(AbstractLoadingActivity activity);
}
