package ru.protei.portal.ui.delivery.client.view.card.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.CardStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 *  Серийный номер платы, статус платы
 */
public class NumberColumn extends ClickColumn<Card> {

    @Inject
    public NumberColumn( Lang lang, CardStateLang stateLang) {
        this.lang = lang;
        this.stateLang = stateLang;
        setStopPropogationElementClassName("number-size");
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "number" );
        columnHeader.setInnerText( lang.cardColumnNumber() );
    }

    @Override
    public void fillColumnValue( Element cell, Card card ) {
        if ( card == null ) {
            return;
        }

        cell.addClassName( "number" );
        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        com.google.gwt.dom.client.Element numberElement = DOM.createElement( "p" );
        numberElement.addClassName( "number-size" );
        numberElement.setInnerText(card.getSerialNumber());
        divElement.appendChild( numberElement );

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        CaseState caseState = card.getState();
        stateElement.getStyle().setBackgroundColor(caseState == null ? null : caseState.getColor());
        stateElement.setInnerText(caseState == null ? null : stateLang.getStateName(caseState));

        divElement.appendChild( stateElement );

        cell.appendChild( divElement );
    }

    private Lang lang;
    private CardStateLang stateLang;
}
