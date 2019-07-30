package ru.protei.portal.app.portal.client.widget.locale;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.image.NavImageSelector;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.ToggleButton;

/**
 * Группа кнопок языков приложения
 */
public class LocaleBtnGroup extends ToggleBtnGroup<LocaleImage> {

    public LocaleBtnGroup() {
        fillOptions();
    }

    private void fillOptions() {
        String[] languages = LocaleInfo.getAvailableLocaleNames();

        for ( String language : languages ) {
            LocaleImage image = LocaleImage.findByLocale(language);
            if ( image == null ) {
                continue;
            }
            addBtnWithImage(
                    image.getImageUrl(),
                    "btn btn-default no-border",
                    null,
                    image,
                    null
            );
        }
    }

}
