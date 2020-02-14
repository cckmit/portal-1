package ru.protei.portal.ui.document.client.activity.edit;

public interface AbstractDocumentEditActivity {

    void onEquipmentChanged();

    void onDocumentCategoryChanged();

    void onProjectChanged();

    void onDownloadPdf();

    void onDownloadDoc();

    void onApprovedChanged();

    void onCancelClicked();

    void onSaveClicked();

}
