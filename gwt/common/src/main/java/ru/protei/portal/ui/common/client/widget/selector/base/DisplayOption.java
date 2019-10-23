package ru.protei.portal.ui.common.client.widget.selector.base;

/**
 * Объект отображения в селекторе
 */
public class DisplayOption {

    private String name;
    private String info;
    private String style;
    private String icon;
    private String imageSrc;
    private String anchorIcon;
    private String anchorHref;

    public DisplayOption() {}

    public DisplayOption( String name ) {
        this.name = name;
    }

    public DisplayOption( String name, String style, String icon ) {
        this.icon = icon;
        this.name = name;
        this.style = style;
    }

    public DisplayOption( String name, String imageSrc ) {
        this.name = name;
        this.imageSrc = imageSrc;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo( String info ) {
        this.info = info;
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

    public String getAnchorIcon() {
        return anchorIcon;
    }

    public String getAnchorHref() {
        return anchorHref;
    }

    public void setAnchorHref(String anchorHref) {
        this.anchorHref = anchorHref;
    }

    public void setAnchorIcon(String anchorIcon) {
        this.anchorIcon = anchorIcon;
    }

    public void setStyle( String style ) {
        this.style = style;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc( String imageSrc ) {
        this.imageSrc = imageSrc;
    }
}
