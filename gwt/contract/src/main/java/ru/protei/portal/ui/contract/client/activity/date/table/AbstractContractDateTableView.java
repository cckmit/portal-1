package ru.protei.portal.ui.contract.client.activity.date.table;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.ContractDate;

import java.util.List;

public interface AbstractContractDateTableView extends IsWidget {

    void setActivity( AbstractContractDateTableActivity activity );

    void setData(List<ContractDate> values);

    void showEditableColumns(boolean isVisible);

    void removeRow(ContractDate value);

    void addRow(ContractDate value);

    void showWarning(String message);

    void hideWarning();
}
