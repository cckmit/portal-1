package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Билдер доп информации
 */
public class AdditionalColumnBuilder {

    public static AdditionalColumnBuilder make() {
        return new AdditionalColumnBuilder();
    }

    public AdditionalColumnBuilder add ( String icon, String phone, String className ) {
        if ( phone != null ) {
            Element div = DOM.createDiv();
            div.addClassName( className );
            if ( icon != null ) {
                Element b = DOM.createElement("b");
                b.setInnerText( icon );
                div.appendChild( b );
            }
            Element data = DOM.createSpan();
            data.setInnerText( phone );
            div.appendChild( data );
            root.appendChild( div );
        }
        return this;
    }

    public Element toElement() {
        return root;
    }

    private Element root = DOM.createDiv();
}
