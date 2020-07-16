package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_ContractKind;

public class ContractSupportService {

    public static En_ContractKind getContractKind(boolean parentContractExists) {
        return parentContractExists
                ? En_ContractKind.EXPENDITURE
                : En_ContractKind.RECEIPT;
    }
}
