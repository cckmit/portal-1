package ru.protei.portal.mapper;

import ru.protei.portal.core.model.api.ApiContract;
import ru.protei.portal.core.model.api.ApiContractDate;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.struct.Money;

import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class ContractToApiMapper {

    public static ApiContract contractToApi(Contract contract) {
        ApiContract apiContract = new ApiContract();
        apiContract.setRefKey(contract.getRefKey());
        apiContract.setDateSigning(contract.getDateSigning());
        apiContract.setCost(makeCost(contract.getCost()));
        apiContract.setCurrency(contract.getCurrency());
        apiContract.setVat(contract.getVat());
        apiContract.setDescription(contract.getDescription());
        apiContract.setDirectionName(joining(contract.getProductDirections(), ", ", DevUnit::getName));
        apiContract.setMinistryOfDefence(contract.getProjectCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE);
        apiContract.setDates(stream(contract.getContractDates())
                .map(contractDate -> contractDateToApi(contractDate, contract.getCost()))
                .collect(Collectors.toList()));
        return apiContract;
    }

    public static ApiContractDate contractDateToApi(ContractDate contractDate, Money contractCost) {
        ApiContractDate apiContractDate = new ApiContractDate();
        apiContractDate.setType(contractDate.getType());
        apiContractDate.setDate(contractDate.getDate());
        apiContractDate.setCost(makeCost(contractDate.getCost()));
        apiContractDate.setCostPercent(makePercent(contractCost, contractDate.getCost()));
        apiContractDate.setCurrency(contractDate.getCurrency());
        apiContractDate.setComment(contractDate.getComment());
        return apiContractDate;
    }

    private static Double makeCost(Money money) {
        if (money == null) {
            return null;
        }
        double dMoney = (double) money.getFull();
        return dMoney / 100.0;
    }

    private static Double makePercent(Money total, Money part) {
        if (total == null || part == null) {
            return null;
        }
        double dTotal = (double) total.getFull();
        double dPart = (double) part.getFull();
        double ratio = dPart / dTotal;
        double percent = ratio * 100.0;
        return percent;
    }
}
