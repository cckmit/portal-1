package ru.protei.portal.ui.crm.client.widget.localeselector;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.image.NavImageSelector;

/**
 * Селектор языков приложения
 */
public class LocaleSelector extends NavImageSelector<LocaleImage> {

    @Inject
    public void onInit() {
        setDisplayOptionCreator( value -> new DisplayOption( LocaleInfo.getLocaleNativeDisplayName( value.getLocale() ), value.getImageUrl() ) );
        fillOptions();
        addStyleName( "locale-selector" );
    }

    private void fillOptions() {
        String[] languages = LocaleInfo.getAvailableLocaleNames();

        for ( String language : languages ) {
            LocaleImage image = LocaleImage.findByLocale(language);
            if ( image == null ) {
                continue;
            }
            addOption( image );
        }
    }
}
