package ru.protei.portal.ui.contract.client.activity.table.concise;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Contract;

import java.util.List;

public interface AbstractContractConciseTableView extends IsWidget {

    void setActivity(AbstractContractConciseTableActivity activity);

    void clearRecords();

    void setData(List<Contract> data);
}
