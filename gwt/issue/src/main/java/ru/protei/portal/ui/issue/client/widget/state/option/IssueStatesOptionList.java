package ru.protei.portal.ui.issue.client.widget.state.option;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.issue.client.widget.state.StateModel;

import java.util.List;

/**
 * Селектор списка состояний обращения
 */
public class IssueStatesOptionList extends OptionList<En_CaseState> implements ModelSelector<En_CaseState> {

    @Inject
    public void init( StateModel stateModel) {
        stateModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< En_CaseState > states ) {
        clearOptions();
        states.forEach(state -> {
            addOption( lang.getStateName( state ), state, "form-group col-xs-4 option-" + state.toString().toLowerCase() );
            setEnsureDebugId(state, DebugIdsHelper.ISSUE_STATE.byId(state.getId()));
        });
    }

    @Override
    public void refreshValue() {}

    @Inject
    En_CaseStateLang lang;
}