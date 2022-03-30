package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_ContractKind;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype.CalculationTypeModel;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;
import java.util.List;

public interface AbstractContractEditView extends IsWidget {

    void setActivity(AbstractContractEditActivity activity);

    HasEnabled saveEnabled();

    HasValue<MoneyWithCurrencyWithVat> cost();

    HasValue<String> number();

    HasValue<En_ContractType> type();

    void setKind(En_ContractKind kind);

    HasValue<CaseState> state();

    HasValue<CalculationType> calculationType();

    HasEnabled calculationTypeEnabled();

    HasValue<String> description();

    HasValue<PersonShortView> curator();

    HasValue<Date> dateSigning();

    HasValue<Date> dateEndWarranty();

    HasValue<Date> dateExecution();

    HasValue<Date> dateValidDate();

    HasValue<Long> dateValidDays();

    HasWidgets getContractDateTableContainer();

    HasValue<List<ContractSpecification>> contractSpecifications();

    HasValidable validateContractSpecifications();

    HasValue<EntityOption> organization();

    HasValue<ContractInfo> contractParent();

    HasEnabled costEnabled();

    HasValue<ProjectInfo> project();

    void setProjectManager(String value);

    HasValue<PersonShortView> contractSignManager();

    HasValue<Contractor> contractor();

    HasValue<String> deliveryNumber();

    HasEnabled contractorEnabled();

    void setDirections(String value);

    void setOrganization(String organization);

    void initCuratorsSelector(List<String> contractCuratorsDepartmentsIds);

    HasVisibility tagsVisibility();

    HasVisibility tagsButtonVisibility();

    HasWidgets tagsContainer();

    HasWidgets expenditureContractsContainer();

    HasVisibility expenditureContractsVisibility();
}
