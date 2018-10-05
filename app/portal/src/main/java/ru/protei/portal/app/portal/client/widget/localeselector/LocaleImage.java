package ru.protei.portal.app.portal.client.widget.localeselector;

public enum LocaleImage {
    RU("ru", "images/ru.png"), EN("en", "images/en.png");

    LocaleImage( String locale, String imageUrl ) {
        this.locale = locale;
        this.imageUrl = imageUrl;
    }

    public String getLocale() {
        return locale;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static LocaleImage findByLocale( String locale ) {
        for ( LocaleImage value : values() ) {
            if ( value.locale.equalsIgnoreCase( locale )) {
                return value;
            }
        }

        return null;
    }

    private final String locale;
    private final String imageUrl;
}
