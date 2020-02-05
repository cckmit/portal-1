package ru.protei.portal.ui.document.client.activity.form;

public interface AbstractDocumentFormActivity {

    void onApprovedChanged();

    void onEquipmentChanged();

    void onDocumentCategoryChanged();

    void onProjectChanged();

    void onDecimalNumberChanged();

    void onDownloadPdf();

    void onDownloadDoc();
}
