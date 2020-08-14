package ru.protei.portal.ui.contract.client.widget.contractor.search;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

public interface AbstractContractorSearchView extends IsWidget, HasValidable {
    void setActivity(AbstractContractorSearchActivity activity);

    void setOrganization(String value);

    HasValue<String> contractorInn();

    HasValue<String> contractorKpp();

    HasValue<String> contractorFullName();

    HasValue<Contractor> contractor();

    void setSearchResult(List<Contractor> result);

    void reset();
}
