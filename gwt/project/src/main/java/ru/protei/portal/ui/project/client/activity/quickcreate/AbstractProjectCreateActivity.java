package ru.protei.portal.ui.project.client.activity.quickcreate;

import ru.protei.portal.core.model.struct.ProductDirectionInfo;

/**
 * Активность создания проекта с минимальным набором параметров
 */
public interface AbstractProjectCreateActivity {
    void onSaveClicked();
    void onResetClicked();
    void onDirectionChanged(ProductDirectionInfo info);
}
