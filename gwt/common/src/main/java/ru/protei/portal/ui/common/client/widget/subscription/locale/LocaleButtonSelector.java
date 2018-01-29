package ru.protei.portal.ui.common.client.widget.subscription.locale;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;
import java.util.List;

/**
 * Селектор локалей
 */
public class LocaleButtonSelector extends ButtonSelector<String>{

    @Inject
    public void onInit() {
        hasNullValue = false;
        setDisplayOptionCreator( value -> new DisplayOption( value, "selector-option-" + value, null ));
        fillOptions();
    }

    private void fillOptions(){
        clearOptions();
        locales.forEach(this::addOption);
    }

    private final List<String> locales = Arrays.asList("ru", "en");
}
