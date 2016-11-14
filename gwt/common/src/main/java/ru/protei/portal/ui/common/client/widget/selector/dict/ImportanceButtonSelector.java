package ru.protei.portal.ui.common.client.widget.selector.dict;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Created by bondarenko on 10.11.16.
 */
public class ImportanceButtonSelector extends ButtonSelector<En_ImportanceLevel> {

    @Inject
    public void init( ) {
        addOption(lang.basicImportance(), En_ImportanceLevel.BASIC);
        addOption(lang.importantImportance(), En_ImportanceLevel.IMPORTANT);
        addOption(lang.criticalImportance(), En_ImportanceLevel.CRITICAL);
        addOption(lang.cosmeticImportance(), En_ImportanceLevel.COSMETIC);
    }

    @Inject
    Lang lang;

}
