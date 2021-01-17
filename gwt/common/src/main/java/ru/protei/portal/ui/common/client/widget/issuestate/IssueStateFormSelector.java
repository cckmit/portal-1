package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class IssueStateFormSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init( StateModel model ) {
        setAsyncModel( model );
        setSearchEnabled(false);
        setItemRenderer( value -> value == null ? defaultValue :
                         "<i class='fas fa-circle m-r-5 state-" + makeCaseStateStyle(value) +
                         "' style='color:" + value.getColor() + "'></i>" + value.getState());
    }

    @Override
    protected SelectorItem<CaseState> makeSelectorItem(CaseState element, String elementHtml ) {
        PopupSelectorItem<CaseState> item = new PopupSelectorItem();
        item.setName( makeCaseStateName(element));
        item.setTitle( makeCaseStateTitle(element));
        item.setIcon( "fas fa-circle m-r-5 state-" + makeCaseStateStyle(element) );
        item.setIconColor( element.getColor() );
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

    public void setDefaultValue(String value ) {
        this.defaultValue = value;
    }

    private String defaultValue;
}
