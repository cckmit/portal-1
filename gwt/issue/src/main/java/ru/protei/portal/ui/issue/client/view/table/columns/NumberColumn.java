package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Номер"
 */
public class NumberColumn extends ClickColumn<CaseShortView> {

    @Inject
    public NumberColumn( Lang lang, En_CaseStateLang caseStateLang) {
        this.lang = lang;
        this.caseStateLang = caseStateLang;
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

        if ( value.getImpLevel() != null ) {
            com.google.gwt.dom.client.Element i = DOM.createElement( "i" );
            i.addClassName(ImportanceStyleProvider.getImportanceIcon(En_ImportanceLevel.getById(value.getImpLevel())) + " center");
            divElement.appendChild( i );
        }

        com.google.gwt.dom.client.Element numberElement = DOM.createElement( "p" );
        numberElement.addClassName( "number-size" );
        numberElement.setInnerText( value.getCaseNumber().toString() );
        divElement.appendChild( numberElement );

        com.google.gwt.dom.client.Element stateElement = DOM.createElement( "p" );
        stateElement.addClassName( "label label-" + En_CaseState.getById( value.getStateId() ).toString().toLowerCase() );
        stateElement.setInnerText( caseStateLang.getStateName( En_CaseState.getById( value.getStateId() ) ) );
        divElement.appendChild( stateElement );

        cell.appendChild( divElement );
    }

    Lang lang;

    En_CaseStateLang caseStateLang;
}
