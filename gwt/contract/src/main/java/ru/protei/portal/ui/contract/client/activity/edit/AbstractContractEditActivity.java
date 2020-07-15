package ru.protei.portal.ui.contract.client.activity.edit;

public interface AbstractContractEditActivity {

    void onSaveClicked();

    void onCancelClicked();

    void onTypeChanged();

    void onOrganizationChanged();

    void onContractParentChanged();

    void refreshProjectSpecificFields();
}
