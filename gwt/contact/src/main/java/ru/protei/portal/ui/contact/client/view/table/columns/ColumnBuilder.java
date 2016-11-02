package ru.protei.portal.ui.contact.client.view.table.columns;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Билдер телефонов
 */
public class ColumnBuilder {

    public static ColumnBuilder make() {
        return new ColumnBuilder();
    }

    public ColumnBuilder add ( String icon, String phone ) {
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
    };

    public Element toElement() {
        return root;
    }

    private Element root = DOM.createDiv();
}
