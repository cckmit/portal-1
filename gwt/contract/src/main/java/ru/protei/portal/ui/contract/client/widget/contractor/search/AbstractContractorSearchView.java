package ru.protei.portal.ui.contract.client.widget.contractor.search;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.ContractorPair;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

public interface AbstractContractorSearchView extends IsWidget, HasValidable {
    void setActivity(AbstractContractorSearchActivity activity);

    HasValue<String> contractorINN();

    HasValue<String> contractorKPP();

    HasValue<ContractorPair> contractor();

    void setSearchResult(List<ContractorPair> result);

    void reset();
}
