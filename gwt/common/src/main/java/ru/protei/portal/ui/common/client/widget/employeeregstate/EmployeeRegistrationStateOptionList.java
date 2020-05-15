package ru.protei.portal.ui.common.client.widget.employeeregstate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

import java.util.Arrays;
import java.util.List;

public class EmployeeRegistrationStateOptionList extends OptionList<En_CaseState> {

    @Inject
    public void onInit() {
        fillOptions(Arrays.asList(En_CaseState.ACTIVE, En_CaseState.CREATED, En_CaseState.DONE));
    }

    public void fillOptions( List< En_CaseState > states ) {
        clearOptions();
        states.forEach(state ->
                addOption( lang.getStateName( state ), state,
                        "inline m-r-5 option-" + state.toString().toLowerCase() ));
    }

    @Inject
    private En_CaseStateLang lang;
}