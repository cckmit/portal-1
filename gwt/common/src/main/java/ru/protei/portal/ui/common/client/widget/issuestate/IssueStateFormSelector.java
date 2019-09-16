package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.issuestate.StateModel;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class IssueStateFormSelector extends FormSelector<En_CaseState> implements SelectorWithModel<En_CaseState> {

    @Inject
    public void init(StateModel model, En_CaseStateLang lang) {
        this.model = model;
        this.lang = lang;
    }

    @Override
    public void setValue(En_CaseState value, boolean fireEvents) {
        onValueSet(value);
        super.setValue(value, fireEvents);
    }

    @Override
    public void fillOptions(List<En_CaseState> options) {
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

    private void onValueSet(En_CaseState caseState) {
        if (model == null || workflow == null) {
            // widget has not been configured properly yet
            return;
        }
        setDisplayOptionCreator(makeDisplayOptionCreator(workflow));
        model.subscribe(this, workflow, caseState);
    }

    private DisplayOptionCreator<En_CaseState> makeDisplayOptionCreator(En_CaseStateWorkflow workflow) {
        if (workflow == En_CaseStateWorkflow.NO_WORKFLOW) {
            return caseState -> new DisplayOption(makeCaseStateName(caseState));
        }
        return new DisplayOptionCreator<En_CaseState>() {
            @Override
            public DisplayOption makeDisplayOption(En_CaseState caseState) {
                return new DisplayOption(makeCaseStateName(caseState));
            }
            @Override
            public DisplayOption makeDisplaySelectedOption(En_CaseState caseState) {
                return new DisplayOption(makeCaseStateName(caseState), "", "far fa-dot-circle case-state-item");
            }
        };
    }

    private String makeCaseStateName(En_CaseState caseState) {
        return caseState == null ? defaultValue : lang.getStateName(caseState);
    }

    private En_CaseStateLang lang;
    private En_CaseStateWorkflow workflow;
    private StateModel model;
    private String defaultValue;
}
