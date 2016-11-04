package ru.protei.portal.ui.common.client.widget.selector.dict;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Created by Michael on 28.10.16.
 */
public class GenderButtonSelector extends ButtonSelector<En_Gender> {

    @Inject
    public void init( ) {
        setSearchEnabled( false );
        setSearchAutoFocus( true );

        addOption(lang.genderUndefined(), En_Gender.UNDEFINED);
        addOption(lang.genderFemale(), En_Gender.FEMALE);
        addOption(lang.genderMale(), En_Gender.MALE);
    }


//    public void setHasAnyValue( boolean hasAnyValue ) {
//        this.hasAnyValue = hasAnyValue;
//    }

    @Inject
    Lang lang;

//    private boolean hasAnyValue = true;

}
