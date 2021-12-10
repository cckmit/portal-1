package ru.protei.portal.ui.common.client.util;

import com.google.gwt.user.client.Window;

import static com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale;
import static ru.protei.portal.core.model.util.CrmConstants.LocaleTags.EN;

public class LocaleUtils {

    public static void changeLocale( String language ) {
        String href = Window.Location.getHref();
        String newHref;

        if ( !href.contains( "locale" ) ) {
            newHref = href.contains( "#" ) ? href.replace( "#", "?locale=" + language + "#" ) : href + "?locale=" + language;
        } else {
            String locale = getCurrentLocale().getLocaleName();
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
        return EN.equals(getCurrentLocale().getLocaleName());
    }
}
