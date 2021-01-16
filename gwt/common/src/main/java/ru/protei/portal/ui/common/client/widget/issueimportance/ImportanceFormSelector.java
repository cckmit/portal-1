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

    private ImportanceModel model;

    @Inject
    public void init( ImportanceModel model ) {
        this.model = model;
        setAsyncModel( model );
        super.setSearchEnabled(false);
        setItemRenderer( value -> value == null ? defaultValue :
                         "<i class='" + ImportanceStyleProvider.getImportanceIcon(value.getCode()) + " selector' " +
                         "style='background-color:" + value.getColor() + "'></i>" + value.getCode());
    }

    @Override
    protected SelectorItem<ImportanceLevel> makeSelectorItem(ImportanceLevel element, String elementHtml ) {
        PopupSelectorItem<ImportanceLevel> item = new PopupSelectorItem();
        item.setName( element.getCode() );
        item.setTitle( element.getInfo() );
        item.setStyle( "importance-item" );
        item.setIcon( ImportanceStyleProvider.getImportanceIcon(element.getCode()) + " selector");
        item.setIconBackgroundColor( element.getColor() );
        return item;
    }

    public void setDefaultValue(String value ) {
        this.defaultValue = value;
    }

    private String defaultValue;
}
