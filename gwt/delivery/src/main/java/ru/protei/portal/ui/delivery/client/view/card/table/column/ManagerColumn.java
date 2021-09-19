package ru.protei.portal.ui.delivery.client.view.card.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Ответственный"
 */
public class ManagerColumn extends ClickColumn<Card> {

    @Inject
    public ManagerColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "manager" );
        columnHeader.setInnerText( lang.cardColumnManager() );
    }

    @Override
    public void fillColumnValue( Element cell, Card card ) {
        if ( card == null ) {
            return;
        }

        cell.addClassName( "manager" );

        if (card.getManager() != null) {
            com.google.gwt.dom.client.Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(StringUtils.emptyIfNull(card.getManager().getDisplayShortName() ));
            cell.appendChild(managerElement);
        }
    }

    Lang lang;
}
