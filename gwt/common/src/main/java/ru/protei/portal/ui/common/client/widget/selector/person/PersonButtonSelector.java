package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор person
 */
public class PersonButtonSelector extends ButtonSelector< PersonShortView > implements ModelSelector<PersonShortView> {

    @Inject
    public void init() {
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

    @Override
    public void setValue( PersonShortView value ) {
        if( personModel.isPushing() ){
            deferred = value;
        }else
            super.setValue( value );
    }


    public void fillOptions( List< PersonShortView > persons ){
        clearOptions();

        if (defaultValue != null) {
            addOption(null);
        }

        persons.forEach(this::addOption);

        super.setValue( deferred );
        deferred = null;
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setFired ( Boolean value ) { this.fired = value; }

    public void updateCompany( Company company ){
        if( company == null ) {
            clearOptions();
            return;
        }
//        this.company = company;
//        updatePersons();
        personModel.requestPersonList( company, fired, this::fillOptions );
    }

    private void updatePersons(){
    }

    @Inject
    Lang lang;

    @Inject
    PersonModel personModel;

    PersonShortView deferred;

    private String defaultValue;
    private Boolean fired = null;
}
