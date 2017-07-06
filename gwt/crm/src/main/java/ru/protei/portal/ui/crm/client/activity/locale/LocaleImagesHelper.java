package ru.protei.portal.ui.crm.client.activity.locale;

/**
 * Вспомогательный класс для списка иконок языков приложния
 */
public class LocaleImagesHelper {

    public static LocaleModel getModel( String locale ) {
        LocaleModel result = null;
        if ( locale.equals( "ru" ) ) {
            result = new LocaleModel( locale, RU_ICON, firstUpperCase( locale ) );
        } else if ( locale.equals( "en" ) ) {
            result = new LocaleModel( locale, EN_ICON, firstUpperCase( locale ) );
        }
        return result;
    }

    public static String firstUpperCase( String word ) {
        if ( word == null || word.isEmpty() ) return "";
        return word.substring( 0, 1 ).toUpperCase() + word.substring( 1 );
    }

    public static final String RU_ICON = "images/ru.png";
    public static final String EN_ICON = "images/en.png";

    public static class LocaleModel {
        public LocaleModel( String lang, String icon, String name ) {
            this.lang = lang;
            this.icon = icon;
            this.name = name;
        }

        public String lang;
        public String icon;
        public String name;
    }
}
