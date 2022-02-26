package ru.protei.portal.core.model.dict;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.ArrayList;
import java.util.List;

/**
 * Состояния договоров
 */
public class ContractState {

    private static String[] states = new String[]{"agreement", "have an original", "copies send to customer",
                                                  "waiting for copies from customer", "waiting for original",
                                                  "cancelled", "eds signed", "signed on site"};
    @JsonIgnore
    private static final List<CaseState> allContractStates;
    @JsonIgnore
    private static final List<CaseState> contractStatesByDefault;
    @JsonIgnore
    private static final List<CaseState> openedContractStates;

    static {
        allContractStates = new ArrayList<>();
        for (String state : states) {
            allContractStates.add(new CaseState(state));
        }

        contractStatesByDefault = new ArrayList<>();
        for (String state : states) {
            if (state.equals("cancelled")) {
                continue;
            }

            contractStatesByDefault.add(new CaseState(state));
        }

        openedContractStates = new ArrayList<>();
        for (String state : states) {
            if (state.equals("agreement" ) || state.equals("cancelled")) {
                continue;
            }

            openedContractStates.add(new CaseState(state));
        }
    }

    @JsonIgnore
    public static List<CaseState> allContractStates() { return allContractStates; }
    @JsonIgnore
    public static List<CaseState> contractStatesByDefault() { return contractStatesByDefault; }
    @JsonIgnore
    public static List<CaseState> getOpenedContractStates() { return openedContractStates; }
}
