package ru.protei.portal.ui.project.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка с информацией о проекте
 */
public class InfoColumn extends ClickColumn< ProjectInfo > {

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "info" );
        columnHeader.setInnerText( lang.projectInfo() );
    }

    @Override
    public void fillColumnValue( Element cell, ProjectInfo value ) {
        cell.addClassName( "info" );

        StringBuilder content = new StringBuilder();
        content.append( "<b>" ).append( value.getName() ).append( "</b><br/>" )
                .append(StringUtils.emptyIfNull(value.getDescription()));

        cell.setInnerHTML( content.toString() );
    }

    @Inject
    Lang lang;
}
