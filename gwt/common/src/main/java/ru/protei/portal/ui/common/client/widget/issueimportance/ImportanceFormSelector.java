package ru.protei.portal.ui.common.client.widget.issueimportance;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

/**
 * Селектор критичности кейсов
 */
public class ImportanceFormSelector extends FormPopupSingleSelector<ImportanceLevel> {

    @Inject
    public void init( ImportanceModel model ) {
        setSearchEnabled(false);
        setAsyncModel( model );
        setItemRenderer( value -> value == null ? defaultValue : value.getCode() );
        setValueRenderer( value -> value == null ? defaultValue :
                         "<i class='" + makeValue(value) +
                         "' style='background-color:" + value.getColor() + "'></i>" + value.getCode());
    }

    @Override
    protected SelectorItem<ImportanceLevel> makeSelectorItem(ImportanceLevel element, String elementHtml ) {
        PopupSelectorItem<ImportanceLevel> item = new PopupSelectorItem();
        item.setName( element.getCode() );
        item.setTitle( element.getCode() );
        item.setStyle( "importance-item" );
        item.setIcon( makeValue(element) );
        item.setIconBackgroundColor( element.getColor() );
        return item;
    }

    private String makeValue(ImportanceLevel value) {
        return ImportanceStyleProvider.getImportanceIcon(value.getCode()) + " selector";
    }

    public void setDefaultValue(String value ) {
        this.defaultValue = value;
    }

    private String defaultValue;
}
