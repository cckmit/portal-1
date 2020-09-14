package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

public interface AbstractContractEditActivity {

    void onSaveClicked();

    void onCancelClicked();

    void onTypeChanged();

    void onOrganizationChanged();

    void onContractParentChanged();

    void onProjectChanged();

    void onCreateSecondContractToggle(boolean enabled);

    void onSecondContractOrganizationChanged();

    void onDateSigningChanged(Date date);

    void onDateValidChanged(Date date);

    void onDateValidChanged(Long days);

    void onAddTagsClicked(IsWidget target);

}
