package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Селектор person
 */
public class PersonFormSelector extends FormPopupSingleSelector<PersonShortView>
{

    @Inject
    public void init(  ) {
        setItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }

    protected SelectorItem makeSelectorItem( PersonShortView value, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        if (value == null) {
            item.setName( defaultValue );
            item.setTitle( defaultValue );
            return item;
        }
        item.setName( elementHtml );
        item.setTitle( value.getDisplayName() );

        item.setStyle( value.isFired() ? "not-active" : "" );
        item.setIcon( value.isFired() ? "fa fa-ban ban" : "" );
        return item;
    }

    private static final Logger log = Logger.getLogger( PersonFormSelector.class.getName() );
}
