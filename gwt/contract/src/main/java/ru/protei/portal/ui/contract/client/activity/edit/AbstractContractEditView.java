package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_ContractKind;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.contract.client.widget.contractdates.list.ContractDatesList;

import java.util.Date;
import java.util.List;

public interface AbstractContractEditView extends IsWidget {

    void setActivity(AbstractContractEditActivity activity);

    HasEnabled saveEnabled();

    HasValue<MoneyWithCurrencyWithVat> cost();

    HasValue<String> number();

    HasValue<En_ContractType> type();

    void setKind(En_ContractKind kind);

    HasValue<En_ContractState> state();

    HasValue<String> description();

    HasValue<PersonShortView> curator();

    HasValue<Date> dateSigning();

    HasValue<Date> dateValidDate();

    HasValue<Long> dateValidDays();

    HasWidgets getContractDateTableContainer();

    HasValue<List<ContractSpecification>> contractSpecifications();

    HasValidable validateContractSpecifications();

    HasValue<EntityOption> organization();

    HasValue<EntityOption> contractParent();

    HasEnabled costEnabled();

    HasValue<ProjectInfo> project();

    HasValue<PersonShortView> contractSignManager();

    HasValue<Contractor> contractor();

    HasValue<String> deliveryNumber();

    HasEnabled contractorEnabled();

    HasValue<PersonShortView> projectManager();

    void setDirections(String value);

    void setOrganization(String organization);

    HasVisibility tagsVisibility();

    HasVisibility tagsButtonVisibility();

    HasWidgets tagsContainer();

    HasWidgets expenditureContractsContainer();

    HasVisibility expenditureContractsVisibility();
}
