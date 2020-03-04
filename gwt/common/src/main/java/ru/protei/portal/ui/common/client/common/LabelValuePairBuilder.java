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

    public LabelValuePairBuilder addIconValuePair(String icon, String value, String className ) {
        if ( value != null ) {
            root.addClassName( className );
            if ( icon != null ) {
                Element i = DOM.createElement("i");
                i.setClassName( icon );
                root.appendChild( i );
            }
            Element data = DOM.createSpan();
            data.setInnerText( value );
            root.appendChild( data );
        }
        return this;
    }

    public LabelValuePairBuilder addLabelValuePair(String label, String value, String className ) {
        if ( value != null ) {
            root.addClassName( className );
            if ( label != null ) {
                Element b = DOM.createElement("b");
                b.setInnerText( label );
                root.appendChild( b );
            }
            Element data = DOM.createSpan();
            data.setInnerText( value );
            root.appendChild( data );
        }
        return this;
    }

    public Element toElement() {
        return root;
    }

    private Element root = DOM.createDiv();
}
