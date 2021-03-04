package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

import java.util.Date;

public interface AbstractContractEditActivity{

    void onSaveClicked();

    void onCancelClicked();

    void onTypeChanged();

    void onOrganizationChanged();

    void onContractParentChanged();

    void onProjectChanged();

    void onCostChanged();

    void onDateSigningChanged(Date date);

    void onDateValidChanged(Date date);

    void onDateValidChanged(Long days);

    void onAddTagsClicked(IsWidget target);

    void onAddDateClicked();
}
