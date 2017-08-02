package ru.protei.portal.ui.crm.client.widget.localeselector;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.image.NavImageSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Селектор языков приложения
 */
public class LocaleSelector extends NavImageSelector<LocaleImage> {

    @Inject
    public void onInit(){
        fillOptions();
    }

    private void fillOptions() {
        String[] languages = LocaleInfo.getAvailableLocaleNames();

        for ( String language : languages ) {
            LocaleImage image = LocaleImage.findByLocale(language);
            if ( image == null ) {
                continue;
            }
            addOption( image.getLocale(), image, image.getImageUrl() );
        }
    }
}
