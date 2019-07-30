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

    public ContactColumnBuilder add ( String icon, String value ) {
        if ( value == null ) {
            return this;
        }

        Element small = DOM.createElement("small");
        small.setInnerHTML("<i class='" + icon + "'></i>" + value);
        root.appendChild( small );
        return this;
    }

    public Element toElement() {
        return root;
    }

    private Element root = DOM.createDiv();
}
