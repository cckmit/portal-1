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
public class PersonFormSelector extends FormPopupSingleSelector<PersonShortView> implements Refreshable
{

    @Inject
    public void init( PersonModel model ) {
        this.model = model;
        setModel( model );
        setItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }

    protected SelectorItem makeSelectorItem( PersonShortView value, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        if (value == null) {
            item.setName( defaultValue );
            return item;
        }
        item.setName( elementHtml );

        item.setStyle( value.isFired() ? "not-active" : "" );
        item.setIcon( value.isFired() ? "fa fa-ban ban" : "" );
        return item;
    }

    public void setFired ( boolean fired ) {
        this.fired = fired;
    }

    private static final Logger log = Logger.getLogger( PersonFormSelector.class.getName() );

    @Override
    public void refresh() {
    }

    public void updateCompanies(Set<Long> companyIds) {
        if (model != null) {
            model.updateCompanies(this, null, companyIds, fired);
        }
    }

    private PersonModel model;

    private boolean fired = false;
}
