package ru.protei.portal.ui.project.client.activity.preview;

/**
 * Абстракция активности превью проекта
 */
public interface AbstractProjectPreviewActivity {
    void onFullScreenPreviewClicked();
    void onGoToProjectClicked();

    void onContractLinkClicked();

    void onProductLinkClicked();
}
