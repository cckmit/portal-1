package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class IssueStateFormSelector extends FormSelector<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init(StateModel model, En_CaseStateLang lang) {
        this.model = model;
        this.lang = lang;
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
            return caseState -> new DisplayOption(makeCaseStateName(caseState), "", "fas fa-circle m-r-5 state-" +
                    caseState.getState().replaceAll("[.-]", "_").toLowerCase());
        }
        return new DisplayOptionCreator<CaseState>() {
            @Override
            public DisplayOption makeDisplayOption(CaseState caseState) {
                return new DisplayOption(makeCaseStateName(caseState));
            }
            @Override
            public DisplayOption makeDisplaySelectedOption(CaseState caseState) {
                return new DisplayOption(makeCaseStateName(caseState), "", "far fa-dot-circle case-state-item");
            }
        };
    }

    private String makeCaseStateName(CaseState caseState) {
        return caseState == null ? defaultValue : lang.getStateName(caseState);
    }

    private En_CaseStateLang lang;
    private En_CaseStateWorkflow workflow;
    private StateModel model;
    private String defaultValue;
}
