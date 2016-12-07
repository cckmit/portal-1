package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Билдер телефонов
 */
public class ContactColumnBuilder {

    public static ContactColumnBuilder make() {
        return new ContactColumnBuilder();
    }

    public ContactColumnBuilder add ( String icon, String phone ) {
        if ( phone != null ) {
            Element div = DOM.createDiv();
            if ( icon != null ) {
                Element i = DOM.createElement("i");
                i.setClassName( icon );
                div.appendChild( i );
            }
            Element label = DOM.createLabel();
            label.setInnerText( phone );
            div.appendChild( label );
            root.appendChild( div );
        }
        return this;
    }

    public Element toElement() {
        return root;
    }

    private Element root = DOM.createDiv();
}
