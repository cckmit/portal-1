package ru.protei.portal.ui.contract.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;

public interface AbstractContractPreviewView extends IsWidget {
    void setActivity(AbstractContractPreviewActivity activity);

    HasWidgets getCommentsContainer();

    void setHeader(String value);

    void setType(String value);

    void setState(String value);

    void setDateSigning(String value);

    void setDateValid(String value);

    void setDescription(String value);

    void setDirection(String value);

    void setContragent(String value);

    void setCurator(String value);

    void setManager(String value);

    void setDates(String value);
}
