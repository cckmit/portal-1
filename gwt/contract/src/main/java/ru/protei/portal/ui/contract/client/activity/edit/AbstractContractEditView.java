package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.struct.ContractDates;
import ru.protei.portal.core.model.struct.CostWithCurrency;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

public interface AbstractContractEditView extends IsWidget {

    void setActivity(AbstractContractEditActivity activity);

    HasEnabled saveEnabled();

    HasValue<CostWithCurrency> cost();

    HasValue<String> number();

    HasValue<En_ContractType> type();

    HasValue<En_ContractState> state();

    HasValue<String> description();

    HasValue<PersonShortView> curator();

    HasValue<PersonShortView> manager();

    HasValue<EntityOption> contragent();

    HasValue<ProductDirectionInfo> direction();

    HasValue<Date> dateSigning();

    HasValue<Date> dateValid();

    HasValue<ContractDates> contractDates();

    HasValue<EntityOption> organization();

    HasValue<EntityOption> contractParent();

    HasEnabled costEnabled();
}
