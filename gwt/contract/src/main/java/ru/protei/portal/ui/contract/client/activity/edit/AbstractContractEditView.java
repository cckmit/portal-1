package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_ContractKind;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AbstractContractEditView extends IsWidget {

    void setActivity(AbstractContractEditActivity activity);

    HasEnabled saveEnabled();

    HasValue<MoneyWithCurrencyWithVat> cost();

    HasValue<String> number();

    HasValue<Set<En_ContractType>> types();

    void setKind(En_ContractKind kind);

    HasValue<En_ContractState> state();

    HasValue<String> description();

    HasValue<PersonShortView> curator();

    HasValue<Date> dateSigning();

    HasValue<Date> dateValidDate();

    HasValue<Long> dateValidDays();

    HasValue<List<ContractDate>> contractDates();

    HasValue<List<ContractSpecification>> contractSpecifications();

    HasValidable validateContractSpecifications();

    HasValue<EntityOption> organization();

    HasValue<EntityOption> contractParent();

    HasEnabled costEnabled();

    HasValue<ProjectInfo> project();

    HasValue<Contractor> contractor();

    HasEnabled contractorEnabled();

    HasValue<PersonShortView> manager();

    HasValue<ProductDirectionInfo> direction();

    void setOrganization(String organization);

    HasValue<Boolean> secondContractCheckbox();

    HasVisibility secondContractCheckboxVisibility();

    HasVisibility secondContractVisibility();

    HasValue<String> secondContractNumber();

    HasValue<EntityOption> secondContractOrganization();

    HasValue<Contractor> secondContractContractor();

    HasEnabled secondContractContractorEnabled();

    void setSecondContractOrganization(String organization);

    HasWidgets expenditureContractsContainer();

    HasVisibility expenditureContractsVisibility();

    void setNotAvailableOrganizations(List<Long> organizationsToHide);

    void setSecondContractNotAvailableOrganizations(List<Long> organizationsToHide);
}
