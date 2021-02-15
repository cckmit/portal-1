package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

/**
 * Селектор списка состояний обращения
 */
public class IssueStatesOptionList extends OptionList<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init(StateOptionsModel stateModel) {
        setSelectorModel(stateModel);
    }

    @Override
    public void fillOptions(List<CaseState> states) {
        clearOptions();
        states.forEach(state -> {
            addOption(state.getState(), state, "inline m-r-5", makeCaseStateTitle(state), state.getColor());
            setEnsureDebugId(state, DebugIdsHelper.ISSUE_STATE.byId(state.getId()));
        });
    }

    @Override
    public void refreshValue() {}

    private String makeCaseStateTitle(CaseState caseState) {
        return caseState == null ? "" : caseState.getInfo();
    }
}
