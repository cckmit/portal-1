package ru.protei.portal.ui.common.client.widget.selector.dict;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Created by Michael on 28.10.16.
 */
public class GenderButtonSelector extends ButtonSelector<En_Gender> {

    @Inject
    public void init( ) {
        setSearchEnabled( false );
        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> new DisplayOption( getName( value )) );

        fillOptions();
    }

    @Override
    public boolean isValid(){
        return !getValue().equals(En_Gender.UNDEFINED);
    }

    private void fillOptions() {
        for ( En_Gender value : En_Gender.values() ) {
            addOption( value );
        }
    }


    private String getName( En_Gender value ) {
        if ( value == null ) {
            return lang.genderUndefined();
        }

        switch ( value ) {
            case MALE:
                return lang.genderMale();
            case FEMALE:
                return lang.genderFemale();
            case UNDEFINED:
                return lang.genderUndefined();

            default:
                return lang.genderUndefined();
        }
    }

    @Inject
    Lang lang;
}
