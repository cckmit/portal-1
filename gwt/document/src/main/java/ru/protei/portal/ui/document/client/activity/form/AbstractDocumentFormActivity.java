package ru.protei.portal.ui.document.client.activity.form;

public interface AbstractDocumentFormActivity {

    void onEquipmentChanged();

    void onDocumentCategoryChanged();

    void onProjectChanged();

    void onDownloadPdf();

    void onDownloadDoc();

    void onApprovedChanged();

    void onCancelClicked();

    void onSaveClicked();

}
