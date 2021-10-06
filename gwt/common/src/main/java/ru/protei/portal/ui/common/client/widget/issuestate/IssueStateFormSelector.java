package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

/**
 * Селектор критичности кейсов
 */
public class IssueStateFormSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init() {
        setSearchEnabled( false );
        setItemRenderer( value -> value == null ? defaultValue : value.getInfo() );
        setValueRenderer( value -> value == null ? defaultValue :
                "<i class='fas" + (En_CaseStateWorkflow.NO_WORKFLOW.equals(stateModel.getWorkflow()) ? " fa-circle" : " fa-dot-circle") +
                        " m-r-5' style='color:" + makeCaseStateColor(value) + "'></i>" + value.getState());
    }

    @Override
    protected SelectorItem<CaseState> makeSelectorItem(CaseState element, String elementHtml) {
        PopupSelectorItem<CaseState> item = new PopupSelectorItem();
        item.setName(makeCaseStateName(element));
        item.setTitle(makeCaseStateTitle(element));
        item.setIcon(En_CaseStateWorkflow.NO_WORKFLOW.equals(stateModel.getWorkflow()) ? "fas fa-circle m-r-5" : "fas fa-dot-circle m-r-5");
        item.setIconColor(makeCaseStateColor(element));
        return item;
    }

    private String makeCaseStateName(CaseState caseState) {
        return caseState == null ? defaultValue : caseState.getState();
    }

    private String makeCaseStateTitle(CaseState caseState) {
        return caseState == null ? "" : caseState.getInfo();
    }

    private String makeCaseStateColor(CaseState caseState) {
        return caseState == null ? "" : caseState.getColor();
    }

    @Override
    public void setValue(CaseState value) {
        super.setValue(value);
        stateModel.setCurrentCaseState(value);
    }

    public void setStateModel(StateModel model) {
        this.stateModel = model;
        setAsyncModel( model );
    }

    private StateModel stateModel;
}
