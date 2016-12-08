package ru.protei.portal.ui.issue.client.widget.state.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;
import ru.protei.portal.ui.issue.client.widget.state.StateModel;

import java.util.List;

/**
 * Селектор списка критичности обращения
 */
public class IssueStatesBtnGroup  extends ToggleBtnGroup<En_CaseState> implements ModelSelector<En_CaseState> {

    @Inject
    public void init( StateModel stateModel) {
        stateModel.subscribe( this );
    }

    @Override
    public void fillOptions( List< En_CaseState > states ) {
        clear();
        states.forEach( state -> addBtn( "width-xs text-center button button-" + state.toString().toLowerCase(),
                lang.getStateName( state ),
                state,
                "col-xs-12 col-sm-6 col-md-4" ) );
    }

    @Inject
    En_CaseStateLang lang;
}