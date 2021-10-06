package ru.protei.portal.ui.delivery.client.view.card.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Дата тестирования
 */
public class TestDateColumn extends ClickColumn<Card> {

    @Inject
    public TestDateColumn(Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "test-date" );
        columnHeader.setInnerText( lang.cardColumnTestDate() );
    }

    @Override
    public void fillColumnValue( Element cell, Card card ) {
        if ( card == null ) {
            return;
        }

        cell.addClassName( "test-date" );

        if (card.getTestDate() != null) {
            com.google.gwt.dom.client.Element testDateDiv = DOM.createDiv();
            testDateDiv.setInnerText( DateFormatter.formatDateOnly(card.getTestDate()) );
            cell.appendChild( testDateDiv );
        }
    }

    Lang lang;
}
