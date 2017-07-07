package ru.protei.portal.ui.crm.client.widget.localeselector;

/**
 * Вспомогательный класс для списка иконок языков приложния
 */
public class LocaleImagesHelper {

    public static ImageModel getModel( String locale ) {
        ImageModel result = null;
        if ( locale.equals( "ru" ) ) {
            result = new ImageModel( locale, RU_ICON, firstCharInUpperCase( locale ) );
        } else if ( locale.equals( "en" ) ) {
            result = new ImageModel( locale, EN_ICON, firstCharInUpperCase( locale ) );
        }
        return result;
    }

    public static String firstCharInUpperCase( String word ) {
        if ( word == null || word.isEmpty() ) return "";
        return word.substring( 0, 1 ).toUpperCase() + word.substring( 1 );
    }

    public static final String RU_ICON = "images/ru.png";
    public static final String EN_ICON = "images/en.png";

    public static class ImageModel {
        public ImageModel( String value, String img, String name ) {
            this.value = value;
            this.img = img;
            this.name = name;
        }

        public String value;
        public String img;
        public String name;
    }
}
