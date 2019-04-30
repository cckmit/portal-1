package ru.protei.portal.ui.common.client.widget.issuestate.optionlist;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.issuestate.StateModel;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

/**
 * Селектор списка состояний обращения
 */
public class IssueStatesOptionList extends OptionList<En_CaseState> implements SelectorWithModel<En_CaseState> {

    @Inject
    public void init( StateModel stateModel) {
        stateModel.subscribeNoWorkflow(this);
    }

    @Override
    public void fillOptions( List< En_CaseState > states ) {
        clearOptions();
        states.forEach(state -> {
            addOption( lang.getStateName( state ), state, "form-group col-md-4 option-" + state.toString().toLowerCase() );
            setEnsureDebugId(state, DebugIdsHelper.ISSUE_STATE.byId(state.getId()));
        });
    }

    @Override
    public void refreshValue() {}

    @Inject
    En_CaseStateLang lang;
}