package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;

/**
 * Билдер почтовых адресов
 */
public class EmailColumnBuilder {

    public static EmailColumnBuilder make() {
        return new EmailColumnBuilder();
    }

    public EmailColumnBuilder add ( String icon, String email ) {

        if ( email != null ) {
            Element div = DOM.createDiv();
            div.addClassName( "contact-record" );
            if ( icon != null ) {
                Element i = DOM.createElement("i");
                i.setClassName( icon );
                div.appendChild( i );
            }
            RegExp regExp = RegExp.compile("\\b.+@.+\\b");
            MatchResult matcher = regExp.exec(email);

            String mailto = matcher != null ? matcher.getGroup(0) : email;

            AnchorElement anchor = DOM.createAnchor().cast();
            anchor.setInnerText( email );

            anchor.setHref( "mailto:" + mailto );
            div.appendChild( anchor );
            root.appendChild( div );
        }
        return this;
    }

    public Element toElement() {
        return root;
    }

    private Element root = DOM.createDiv();
}
