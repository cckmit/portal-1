package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import java.util.List;
import java.util.Set;

/**
 * Селектор person
 */
public class PersonFormSelector extends FormSelector< PersonShortView > implements SelectorWithModel<PersonShortView> {

    @Inject
    public void init(InitiatorModel model) {
        this.model = model;
        setSearchEnabled( true );
        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> {
            if ( value == null ) {
                return new DisplayOption( defaultValue );
            }

            return new DisplayOption(
                    value.getDisplayShortName(),
                    value.isFired() ? "not-active" : "",
                    value.isFired() ? "fa fa-ban ban" : "" );
        } );
    }

    public void fillOptions( List< PersonShortView > persons ){
        clearOptions();
        this.persons = persons;
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setFired ( boolean value ) { this.fired = value; }


    @Override
    protected void showPopup(IsWidget relative) {
        if(persons != null){
            if (defaultValue != null) {
                addOption(null);
            }

            persons.forEach(this::addOption);
            persons = null;
        }

        super.showPopup(relative);
        if(companyIds==null){
            SelectorItem item = new SelectorItem();
            item.setName(lang.initiatorSelectACompany());
            item.getElement().addClassName(UiConstants.Styles.TEXT_CENTER);
            popup.getChildContainer().add(item);
        }
    }

    public void updateCompanies(Set<Long> companyIds) {
        this.companyIds = companyIds;
        if(model!=null){
            model.updateCompanies(this, companyIds, fired);
        }
    }

    @Inject
    Lang lang;

    private InitiatorModel model;

    private String defaultValue;
    private boolean fired = false;
    private Set<Long> companyIds;
    private List<PersonShortView> persons;
}
