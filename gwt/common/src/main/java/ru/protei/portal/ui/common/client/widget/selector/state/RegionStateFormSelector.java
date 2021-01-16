package ru.protei.portal.ui.common.client.widget.selector.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionStateModel;

public class RegionStateFormSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init( RegionStateModel model ) {
        setAsyncModel( model );
        setItemRenderer( value -> value == null ? defaultValue :
                         "<i class='" + lang.getStateIcon(value) + " selector' " +
                         "style='color:" + value.getColor() + "'></i>" + lang.getStateName(value));

    }

    @Override
    protected SelectorItem<CaseState> makeSelectorItem( CaseState element, String elementHtml ) {
        PopupSelectorItem<CaseState> item = new PopupSelectorItem();
        item.setName( elementHtml );
        item.setStyle( "region-state-item" );
        if ( element != null )
            item.setIconColor( element.getColor() );

        return item;
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    @Inject
    En_RegionStateLang lang;

    private String defaultValue;
}