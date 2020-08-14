package ru.protei.portal.mapper;

import ru.protei.portal.core.model.api.ApiContract;
import ru.protei.portal.core.model.api.ApiContractDate;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.ContractDate;

import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class ContractToApiMapper {

    public static ApiContract contractToApi(Contract contract) {
        ApiContract apiContract = new ApiContract();
        apiContract.setRefKey(contract.getRefKey());
        apiContract.setDateSigning(contract.getDateSigning());
        apiContract.setCost(contract.getCost().getFull());
        apiContract.setCurrency(contract.getCurrency());
        apiContract.setVat(contract.getVat());
        apiContract.setDescription(contract.getDescription());
        apiContract.setDirectionName(contract.getDirectionName());
        apiContract.setMinistryOfDefence(contract.getProjectCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE);
        apiContract.setDates(stream(contract.getContractDates())
                .map(ContractToApiMapper::contractDateToApi)
                .collect(Collectors.toList()));
        return apiContract;
    }

    public static ApiContractDate contractDateToApi(ContractDate contractDate) {
        ApiContractDate apiContractDate = new ApiContractDate();
        apiContractDate.setType(contractDate.getType());
        apiContractDate.setDate(contractDate.getDate());
        apiContractDate.setComment(contractDate.getComment());
        return apiContractDate;
    }
}
