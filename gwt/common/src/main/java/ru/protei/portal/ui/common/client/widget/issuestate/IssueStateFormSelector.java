package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

/**
 * Селектор критичности кейсов
 */
public class IssueStateFormSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init( StateModel model ) {
        setSearchEnabled( false );
        setAsyncModel( model );
        setItemRenderer( value -> value == null ? defaultValue : value.getInfo() );
        setValueRenderer( value -> value == null ? defaultValue :
                         "<i class='fas fa-circle m-r-5 state-" + makeCaseStateStyle(value) +
                         "' style='color:" + makeCaseStateColor(value) + "'></i>" + value.getState());
    }

    @Override
    protected SelectorItem<CaseState> makeSelectorItem(CaseState element, String elementHtml ) {
        PopupSelectorItem<CaseState> item = new PopupSelectorItem();
        item.setName( makeCaseStateName(element));
        item.setTitle( makeCaseStateTitle(element));
        item.setIcon( "fas fa-circle m-r-5 state-" + makeCaseStateStyle(element) );
        item.setIconColor( makeCaseStateColor(element) );
        return item;
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

    private String makeCaseStateColor(CaseState caseState) {
        return caseState == null ? "" : caseState.getColor();
    }

    public void setDefaultValue(String value ) {
        this.defaultValue = value;
    }

    private String defaultValue;
}
