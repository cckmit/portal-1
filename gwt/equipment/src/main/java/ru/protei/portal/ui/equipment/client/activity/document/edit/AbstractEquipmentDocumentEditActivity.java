package ru.protei.portal.ui.equipment.client.activity.document.edit;

public interface AbstractEquipmentDocumentEditActivity {

    void onSaveClicked();

    void onCancelClicked();

    void onApproveChanged(boolean isApproved);

    void onDocumentCategoryChanged();
}
