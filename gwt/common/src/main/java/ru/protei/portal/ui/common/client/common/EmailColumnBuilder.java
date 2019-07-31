package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;

import java.util.List;

/**
 * Билдер почтовых адресов
 */
public class EmailColumnBuilder {

    public static EmailColumnBuilder make() {
        return new EmailColumnBuilder();
    }

    public EmailColumnBuilder addSingle ( String icon, String email, String mailto, String className ) {

        if ( email != null) {
            Element div = DOM.createDiv();
            div.addClassName( className );
            if ( icon != null ) {
                Element i = DOM.createElement("i");
                i.setClassName( icon );
                div.appendChild( i );
            }

            AnchorElement anchor = DOM.createAnchor().cast();
            anchor.setInnerText( email );

            anchor.setHref( "mailto:" + mailto );
            div.appendChild( anchor );
            root.appendChild( div );
        }
        return this;
    }

    public EmailColumnBuilder addList (String icon, List<String> emailList, List<String> mailtoList, String className ) {

        if ( emailList != null) {
            Element div = DOM.createDiv();
            div.addClassName( className );
            if ( icon != null ) {
                Element i = DOM.createElement("i");
                i.setClassName( icon );
                div.appendChild( i );
            }

            for (int i = 0; i < emailList.size(); i++) {
                Element spanElement = DOM.createSpan();
                spanElement.setInnerText( i == 0 ? "" : ", ");
                div.appendChild(spanElement);

                AnchorElement anchor = DOM.createAnchor().cast();
                anchor.setInnerHTML( emailList.get(i) );
                anchor.setHref( "mailto:" + mailtoList.get(i));
                div.appendChild( anchor );
            }

            root.appendChild( div );
        }
        return this;
    }

    public Element toElement() {
        return root;
    }

    private Element root = DOM.createDiv();
}
