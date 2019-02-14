package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractType;

public class En_ContractTypeLang {
    public String getName(En_ContractType value) {
        if (value == null)
            return "";

        switch (value) {
            case AFTER_SALES_SERVICE_CONTRACT:
                return lang.contractTypeAfterSalesServiceContract();
            case EXPORT_OF_SERVICE_CONTRACT:
                return lang.contractTypeExportOfServiceContract();
            case GOVERNMENT_CONTRACT:
                return lang.contractTypeGovermentContract();
            case LEASE_CONTRACT:
                return lang.contractTypeLeaseContract();
            case LICENSE_CONTRACT:
                return lang.contractTypeLicenseContract();
            case LICENSE_FRAMEWORK_CONTRACT:
                return lang.contractTypeLicenseFrameworkContract();
            case MUNICIPAL_CONTRACT:
                return lang.contractTypeMunicipalContract();
            case ORDER:
                return lang.contractTypeOrder();
            case PURCHASE_CONTRACT:
                return lang.contractTypePurchaseContract();
            case SUBCONTRACT:
                return lang.contractTypeSubcontract();
            case SUPPLY_AND_WORK_CONTRACT:
                return lang.contractTypeSupplyAndWorkContract();
            case SUPPLY_AND_WORK_FRAMEWORK_CONTRACT:
                return lang.contractTypeSupplyAndWorkFrameworkContract();
            case SUPPLY_CONTRACT:
                return lang.contractTypeSupplyContract();
            case SUPPLY_FRAMEWORK_CONTRACT:
                return lang.contractTypeSupplyFrameworkContract();
            case WORK_CONTRACT:
                return lang.contractTypeWorkContract();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
