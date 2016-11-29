package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.CriticalityStyleBuilder;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Номер"
 */
public class NumberColumn extends ClickColumn< CaseObject > {

    @Inject
    public NumberColumn( Lang lang, En_CaseStateLang caseStateLang) {
        this.lang = lang;
        this.caseStateLang = caseStateLang;
    }
    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "number" );
        columnHeader.setInnerText( lang.issueNumber() );
    }

    @Override
    public void fillColumnValue( Element cell, CaseObject value ) {
        cell.addClassName( "number" );

        com.google.gwt.dom.client.Element i = DOM.createElement( "i" );
        i.addClassName( "importance importance-lg " + En_ImportanceLevel.find( value.getImpLevel() ) );
        CriticalityStyleBuilder.make().addClassName( i, En_ImportanceLevel.find( value.getImpLevel() ) );
        cell.appendChild( i );

        com.google.gwt.dom.client.Element numberElement = DOM.createDiv();
        numberElement.addClassName( "number-size" );
        numberElement.setInnerText( value == null ? "" : value.getCaseNumber().toString() );
        cell.appendChild( numberElement );

        com.google.gwt.dom.client.Element stateElement = DOM.createDiv();
        stateElement.addClassName( "label label-" + En_CaseState.getById( value.getStateId() ).toString() );
        stateElement.setInnerText( caseStateLang.getStateName( En_CaseState.getById( value.getStateId() ) ) );
        cell.appendChild( stateElement );
    }

    Lang lang;

    En_CaseStateLang caseStateLang;
}
