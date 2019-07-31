package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Билдер контактов
 */
public class ContactColumnBuilder {

    public static ContactColumnBuilder make() {
        return new ContactColumnBuilder();
    }

    public ContactColumnBuilder add ( String icon, String phone, String className ) {
        if ( phone != null ) {
            Element div = DOM.createDiv();
            div.addClassName( className );
            if ( icon != null ) {
                Element i = DOM.createElement("i");
                i.setClassName( icon );
                div.appendChild( i );
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
