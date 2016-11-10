package ru.protei.portal.ui.common.client.widget.selector.dict;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Created by bondarenko on 10.11.16.
 */
public class ImportanceButtonSelector extends ButtonSelector<En_ImportanceLevel> {

    @Inject
    public void init( ) {
        setSearchEnabled( false );
        setSearchAutoFocus( true );

        addOption(En_ImportanceLevel.BASIC.getCode(), En_ImportanceLevel.BASIC);
        addOption(En_ImportanceLevel.IMPORTANT.getCode(), En_ImportanceLevel.IMPORTANT);
        addOption(En_ImportanceLevel.CRITICAL.getCode(), En_ImportanceLevel.CRITICAL);
        addOption(En_ImportanceLevel.COSMETIC.getCode(), En_ImportanceLevel.COSMETIC);
    }

}
