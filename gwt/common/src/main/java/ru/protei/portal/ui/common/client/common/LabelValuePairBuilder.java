package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Билдер контактов
 */
public class LabelValuePairBuilder {

    public static LabelValuePairBuilder make() {
        return new LabelValuePairBuilder();
    }

    public LabelValuePairBuilder addIconLabelWithTextPair(String icon, String text, String className ) {
        if ( text != null ) {
            Element div = DOM.createDiv();
            div.addClassName( className );
            if ( icon != null ) {
                Element i = DOM.createElement("i");
                i.setClassName( icon );
                div.appendChild( i );
            }
            Element data = DOM.createSpan();
            data.setInnerText( text );
            div.appendChild( data );
            root.appendChild( div );
        }
        return this;
    }

    public LabelValuePairBuilder addTextLabelWithTextPair ( String iconText, String text, String className ) {
        if ( text != null ) {
            Element div = DOM.createDiv();
            div.addClassName( className );
            if ( iconText != null ) {
                Element b = DOM.createElement("b");
                b.setInnerText( iconText );
                div.appendChild( b );
            }
            Element data = DOM.createSpan();
            data.setInnerText( text );
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
