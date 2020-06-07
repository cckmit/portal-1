package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class IssueStateFormSelector extends FormSelector<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init(StateModel model, Lang lang) {
        this.model = model;
        noSearchResult = lang.searchTerminalState();
    }

    @Override
    public void setValue(CaseState value, boolean fireEvents) {
        onValueSet(value);
        super.setValue(value, fireEvents);
    }

    @Override
    public void fillOptions(List<CaseState> options) {
        clearOptions();
        if (defaultValue != null) {
            addOption(null);
        }
        options.forEach(this::addOption);
    }

    @Override
    public void refreshValue() { /* no need to set value again */ }

    public void setWorkflow(En_CaseStateWorkflow workflow) {
        this.workflow = workflow;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private void onValueSet(CaseState caseState) {
        if (model == null || workflow == null) {
            // widget has not been configured properly yet
            return;
        }
        setDisplayOptionCreator(makeDisplayOptionCreator(workflow));
        model.subscribe(this, workflow, caseState);
    }

    private DisplayOptionCreator<CaseState> makeDisplayOptionCreator(En_CaseStateWorkflow workflow) {
        if (workflow == En_CaseStateWorkflow.NO_WORKFLOW) {
            return caseState -> {
                DisplayOption displayOption = new DisplayOption(makeCaseStateName(caseState), "", "fas fa-circle m-r-5 state-" + makeCaseStateStyle(caseState));
                displayOption.setTitle(makeCaseStateTitle(caseState));
                return displayOption;
            };
        }
        return new DisplayOptionCreator<CaseState>() {
            @Override
            public DisplayOption makeDisplayOption(CaseState caseState) {
                DisplayOption displayOption = new DisplayOption(makeCaseStateName(caseState));
                displayOption.setTitle(makeCaseStateTitle(caseState));
                return displayOption;
            }
            @Override
            public DisplayOption makeDisplaySelectedOption(CaseState caseState) {
                DisplayOption displayOption = new DisplayOption(makeCaseStateName(caseState), "", "far fa-dot-circle case-state-item");
                displayOption.setTitle(makeCaseStateTitle(caseState));
                return displayOption;
            }
        };
    }

    private String makeCaseStateName(CaseState caseState) {
        return caseState == null ? defaultValue : caseState.getState();
    }

    private String makeCaseStateStyle(CaseState caseState) {
        return caseState == null ? "" : CaseStateUtils.makeStyleName(caseState.getState());
    }

    private String makeCaseStateTitle(CaseState caseState) {
        return caseState == null ? "" : caseState.getInfo();
    }

    private En_CaseStateWorkflow workflow;
    private StateModel model;
    private String defaultValue;
}
