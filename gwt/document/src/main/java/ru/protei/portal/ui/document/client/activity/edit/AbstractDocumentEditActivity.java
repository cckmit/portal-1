package ru.protei.portal.ui.document.client.activity.edit;

public interface AbstractDocumentEditActivity {

    void onStateChanged();

    void onSaveClicked();

    void onCancelClicked();

    void onEquipmentChanged();

    void onDocumentCategoryChanged();

    void onProjectChanged();

    void onDecimalNumberChanged();
}
