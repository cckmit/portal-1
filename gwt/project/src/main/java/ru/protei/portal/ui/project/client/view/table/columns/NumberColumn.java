package ru.protei.portal.ui.project.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка с номером проекта
 */
public class NumberColumn extends ClickColumn< ProjectInfo > {

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "number" );
        columnHeader.setInnerText( lang.projectDirection() );
    }

    @Override
    public void fillColumnValue( Element cell, ProjectInfo value ) {
        cell.addClassName( "number" );

        StringBuilder content = new StringBuilder();
        content.append( "<b>" ).append( value.getId() ).append( "</b>" );

        if ( value.getProductDirection() != null ) {
            content.append("<br/>").append( value.getProductDirection().getDisplayText() );
        }
        if (value.getCustomerType() != null) {
            content.append("<br/><i>").append(customerTypeLang.getName(value.getCustomerType())).append("</i>");
        }

        cell.setInnerHTML( content.toString() );
    }

    @Inject
    Lang lang;

    @Inject
    En_CustomerTypeLang customerTypeLang;
}
