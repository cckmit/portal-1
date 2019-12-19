package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.form.FormSelector;

import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.item.PopupSelectorItem;

import java.util.Set;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.contains;

/**
 * Селектор person
 */
public class PersonFormSelector
        extends FormSelector<PersonShortView>  implements Refreshable
{

    @Inject
    public void init( InitiatorModel model ) {
        this.model = model;
        setSelectorModel( model );
        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setSelectorItemRenderer( value -> value == null ? defaultValue : value.getName() );
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

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setFired ( boolean fired ) {
        this.fired = fired;
    }

    private static final Logger log = Logger.getLogger( PersonFormSelector.class.getName() );

    @Override
    public void refresh() {
        PersonShortView value = getValue();
        if (value != null
                && !contains( model.getValues(), value )) {
            setValue( null );
        }
    }

    public void updateCompanies(Set<Long> companyIds) {
        if(model!=null){
            model.updateCompanies(this, companyIds, fired);
        }
    }

    private InitiatorModel model;
    private String defaultValue;
    private boolean fired = false;
}
