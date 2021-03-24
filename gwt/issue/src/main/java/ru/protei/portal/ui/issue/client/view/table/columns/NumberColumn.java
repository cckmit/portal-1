package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;

/**
 * Колонка "Номер"
 */
public class NumberColumn extends ClickColumn<CaseShortView> {

    @Inject
    public NumberColumn( Lang lang ) {
        this.lang = lang;
        setStopPropogationElementClassName("number-size");
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "number" );
        columnHeader.setInnerText( lang.issueNumber() );
    }

    @Override
    public void fillColumnValue( Element cell, CaseShortView value ) {
        if ( value == null ) {
            return;
        }

        cell.addClassName( "number" );
        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        if ( value.getImportanceCode() != null ) {
            com.google.gwt.dom.client.Element i = DOM.createElement( "i" );
            i.addClassName("case-importance");
            i.setInnerText(value.getImportanceCode().substring(0, 1).toUpperCase());
            i.getStyle().setBackgroundColor(value.getImportanceColor());
            i.getStyle().setColor(makeContrastColor(value.getImportanceColor()));
            divElement.appendChild( i );
        }

        com.google.gwt.dom.client.Element numberElement = DOM.createElement( "p" );
        numberElement.addClassName( "number-size" );
        numberElement.setInnerText( value.getCaseNumber().toString() );
        divElement.appendChild( numberElement );

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        stateElement.getStyle().setBackgroundColor(value.getStateColor());
        stateElement.setInnerText(value.getStateName());

        if (!isPauseDateValid(value.getStateId(), value.getPauseDate())) {
            stateElement.addClassName("pause-status-expired-date");
        } else {
            stateElement.removeClassName("pause-status-expired-date");
        }

        divElement.appendChild( stateElement );

        cell.appendChild( divElement );
    }

    private boolean isPauseDateValid(Long currentStateId, Long pauseDate) {
        if (CrmConstants.State.PAUSED != currentStateId) {
            return true;
        }

        if (pauseDate != null && pauseDate > System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

    private Lang lang;
}
