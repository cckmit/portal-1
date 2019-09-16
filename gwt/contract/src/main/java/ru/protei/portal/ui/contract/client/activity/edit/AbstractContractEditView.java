package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.CostWithCurrency;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;
import java.util.List;

public interface AbstractContractEditView extends IsWidget {

    void setActivity(AbstractContractEditActivity activity);

    HasEnabled saveEnabled();

    HasValue<CostWithCurrency> cost();

    HasValue<String> number();

    HasValue<En_ContractType> type();

    HasValue<En_ContractState> state();

    HasValue<String> description();

    HasValue<PersonShortView> curator();

    HasValue<Date> dateSigning();

    HasValue<Date> dateValid();

    HasValue<List<ContractDate>> contractDates();

    HasValue<EntityOption> organization();

    HasValue<EntityOption> contractParent();

    HasEnabled costEnabled();

    HasValue<EntityOption> project();

    HasValue<String> contragent();

    HasValue<String> manager();

    HasValue<String> direction();
}
