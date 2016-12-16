package ru.protei.portal.ui.common.client.widget.selector.base;

/**
 * Объект отображения в селекторе
 */
public class DisplayOption {

    private String name;
    private String style;
    private String icon;

    public DisplayOption() {}

    public DisplayOption( String name ) {
        this.name = name;
    }

    public DisplayOption( String name, String style, String icon ) {
        this.icon = icon;
        this.name = name;
        this.style = style;
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

    public void setStyle( String style ) {
        this.style = style;
    }
}