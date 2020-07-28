package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ContractKind;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.struct.CostWithCurrencyWithVat;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;
import java.util.List;

public interface AbstractContractEditView extends IsWidget {

    void setActivity(AbstractContractEditActivity activity);

    HasEnabled saveEnabled();

    HasValue<CostWithCurrencyWithVat> cost();

    HasValue<String> number();

    HasValue<En_ContractType> type();

    void setKind(En_ContractKind kind);

    HasValue<En_ContractState> state();

    HasValue<String> description();

    HasValue<PersonShortView> curator();

    HasValue<Date> dateSigning();

    HasValue<Long> dateValidDays();

    HasValue<List<ContractDate>> contractDates();

    HasValue<List<ContractSpecification>> contractSpecifications();

    HasValidable validateContractSpecifications();

    HasValue<EntityOption> organization();

    HasValue<EntityOption> contractParent();

    HasEnabled costEnabled();

    HasValue<ProjectInfo> project();

    HasValue<Contractor> contractor();

    HasValue<PersonShortView> manager();

    HasValue<ProductDirectionInfo> direction();

    HasEnabled managerEnabled();

    HasEnabled directionEnabled();

    HasEnabled organizationEnabled();

    HasEnabled contractorEnabled();

    void setOrganization(String organization);

    HasValue<Boolean> secondContractCheckbox();

    HasVisibility secondContractCheckboxVisibility();

    HasVisibility secondContractVisibility();

    HasValue<String> secondContractNumber();

    HasValue<EntityOption> secondContractOrganization();

    HasValue<Contractor> secondContractContractor();

    HasEnabled secondContractContractorEnabled();

    void setSecondContractOrganization(String organization);
}
