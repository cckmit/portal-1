package ru.protei.portal.ui.common.client.widget.selector.base;

/**
 * Объект отображения в селекторе
 */
public class DisplayOption {

    private String name;
    private String style;
    private String icon;
    private String imageSrc;
    private String externalLink;

    public DisplayOption() {}

    public DisplayOption( String name ) {
        this.name = name;
    }

    public DisplayOption( String name, String imageSrc ) {
        this.name = name;
        this.imageSrc = imageSrc;
    }

    public DisplayOption( String name, String style, String icon ) {
        this.name = name;
        this.style = style;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon( String icon ) {
        this.icon = icon;
    }

    public String getStyle() {
        return style;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public void setStyle(String style ) {
        this.style = style;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc( String imageSrc ) {
        this.imageSrc = imageSrc;
    }
}
