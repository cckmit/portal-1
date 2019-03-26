package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class IssueStateButtonSelector extends ButtonSelector<En_CaseState> implements SelectorWithModel<En_CaseState> {

    @Inject
    public void init(StateModel model, En_CaseStateLang lang) {
        this.model = model;
        this.lang = lang;
        subscribeIfReady();
    }

    @Override
    public void setValue(En_CaseState value) {
        super.setValue(value);
    }

    @Override
    public void setValue(En_CaseState value, boolean fireEvents) {
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

    public void setWorkflow(En_CaseStateWorkflow workflow) {
        this.workflow = workflow;
        subscribeIfReady();
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private void subscribeIfReady() {
        if (model == null || workflow == null) {
            return;
        }
        if (workflow == En_CaseStateWorkflow.NO_WORKFLOW) {
            setDisplayOptionCreator(caseState -> new DisplayOption(makeCaseStateName(caseState)));
        } else {
            setDisplayOptionCreator(new DisplayOptionCreator<En_CaseState>() {
                @Override
                public DisplayOption makeDisplayOption(En_CaseState caseState) {
                    return new DisplayOption(makeCaseStateName(caseState));
                }
                @Override
                public DisplayOption makeDisplaySelectedOption(En_CaseState caseState) {
                    return new DisplayOption(makeCaseStateName(caseState), "", "fa fa-dot-circle-o case-state-item");
                }
            });
        }
        model.subscribe(workflow, this);
    }

    private String makeCaseStateName(En_CaseState caseState) {
        return caseState == null ? defaultValue : lang.getStateName(caseState);
    }

    private En_CaseStateLang lang;
    private En_CaseStateWorkflow workflow;
    private StateModel model;
    private String defaultValue;
}
