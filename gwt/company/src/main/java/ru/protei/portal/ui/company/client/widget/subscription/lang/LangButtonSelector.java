package ru.protei.portal.ui.company.client.widget.subscription.lang;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;
import java.util.List;

/**
 * Селектор локалей
 */
public class LangButtonSelector extends ButtonSelector<String>{

    @Inject
    public void onInit() {
        hasNullValue = false;
        fillOptions();
    }

    private void fillOptions(){
        clearOptions();
        locales.forEach(option -> addOption(option, option));
    }

    private final List<String> locales = Arrays.asList("ru", "en");
}
