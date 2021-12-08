package ru.protei.portal.ui.common.client.util;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;

public class LocaleUtils {

    public static void changeLocale( String language ) {
        String href = Window.Location.getHref();
        String newHref;

        if ( !href.contains( "locale" ) ) {
            newHref = href.contains( "#" ) ? href.replace( "#", "?locale=" + language + "#" ) : href + "?locale=" + language;
        } else {
            String locale = LocaleInfo.getCurrentLocale().getLocaleName();
            if ( locale.isEmpty() )
                newHref = href.replace( "#", "?locale=" + language + "#" );
            else {
                String replace = "locale=" + locale;
                newHref = href.replace( replace, "locale=" + language );
            }
        }
        Window.Location.replace( newHref );
    }

    public static boolean isLocaleEn() {
        return LocaleInfo.getCurrentLocale().getLocaleName() != null && LocaleInfo.getCurrentLocale().getLocaleName().contains("en");
    }
}
