package ru.protei.portal.ui.issue.client.widget.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.issue.client.widget.StateModel;

import java.util.List;

/**
 * Селектор статусов обращения
 */
public class IssueStatesButtonSelector extends ButtonSelector<En_CaseState> implements ModelSelector<En_CaseState> {

    @Inject
    public void init( StateModel stateModel) {
        stateModel.subscribe(this);
    }

    @Override
    public void fillOptions(List<En_CaseState> options){
        clearOptions();

        if(defaultValue != null)
            addOption( defaultValue , null );

        options.forEach(option -> addOption(getStateName(option), option));
    }

    private String getStateName(En_CaseState state){
        switch (state){
            case CREATED: return lang.createdCaseState();
            case CLOSED: return lang.closedCaseState();
            case PAUSED: return lang.pausedCaseState();
            case VERIFIED: return lang.verifiedCaseState();
            case REOPENED: return lang.reopenedCaseState();
            case SOLVED_NOAP: return lang.solvedNoapCaseState();
            case SOLVED_FIX: return lang.solvedFixCaseState();
            case SOLVED_DUP: return lang.solvedDupCaseState();
            case IGNORED: return lang.ignoredCaseState();
            case ASSIGNED: return lang.assignedCaseState();
            case ESTIMATED: return lang.estimatedCaseState();
            case DISCUSS: return lang.discussCaseState();
            case PLANNED: return lang.plannedCaseState();
            case ACTIVE: return lang.activeCaseState();
            case DONE: return lang.doneCaseState();
            case TEST: return lang.testCaseState();
            case TEST_LOCAL: return lang.testLocalCaseState();
            case TEST_CUST: return lang.testCustCaseState();
            case DESIGN: return lang.designCaseState();
            default:
                throw new IllegalArgumentException("unknown state");
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

    @Inject
    Lang lang;

}
