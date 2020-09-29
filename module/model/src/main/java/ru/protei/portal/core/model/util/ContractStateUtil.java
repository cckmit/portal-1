package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.dict.En_ContractState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContractStateUtil {

    public static List<En_ContractState> getClosedContractStates() {
        return new ArrayList<En_ContractState>(){{
            add(En_ContractState.AGREEMENT);
            add(En_ContractState.CANCELLED);
        }};
    }

    public static List<En_ContractState> getOpenedContractStates() {
        List<En_ContractState> list = new ArrayList<>(Arrays.asList(En_ContractState.values()));
        list.removeAll(getClosedContractStates());
        return list;
    }
}
