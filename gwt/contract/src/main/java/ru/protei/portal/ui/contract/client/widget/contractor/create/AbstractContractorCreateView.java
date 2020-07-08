package ru.protei.portal.ui.contract.client.widget.contractor.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractContractorCreateView extends IsWidget, HasValidable {
    HasValue<En_Organization> organization();

    HasValue<String> contractorINN();

    HasValue<String> contractorKPP();

    HasValue<String> contractorName();

    HasValue<String> contractorFullname();

    HasValue<String> contractorCountry();

    void reset();
}
