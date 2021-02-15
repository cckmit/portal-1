package ru.protei.portal.ui.common.client.widget.employeeregstate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

import java.util.List;

public class EmployeeRegistrationStateOptionList extends OptionList<CaseState> implements ModelList<CaseState> {

    @Inject
    public void init(EmployeeRegistrationStateModel model) {
        setSelectorModel(model);
    }

    public void fillOptions(List<CaseState> states) {
        clearOptions();
        states.forEach(state ->
                addOption(state.getState(), state, "inline m-r-5", null, state.getColor()));
    }
}