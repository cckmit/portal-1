package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

/**
 * Селектор списка состояний обращения
 */
public class IssueStatesOptionList extends OptionList<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init( StateModel stateModel) {
        stateModel.subscribeNoWorkflow(this);
    }

    @Override
    public void fillOptions( List< CaseState > states ) {
        clearOptions();
        states.forEach(state -> {
            addOption( lang.getStateName( state ), state, "inline m-r-5 option-" +
                    state.getState().replaceAll("[.-]", "_").toLowerCase() );
            setEnsureDebugId(state, DebugIdsHelper.ISSUE_STATE.byId(state.getId()));
        });
    }

    @Override
    public void refreshValue() {}

    @Inject
    En_CaseStateLang lang;
}