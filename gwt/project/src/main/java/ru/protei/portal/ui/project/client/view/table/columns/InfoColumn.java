package ru.protei.portal.ui.project.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
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

        String content = "<b>" + value.getName() + "</b><br/><small>" +
                value.getDescription() + "</small>";

        cell.setInnerHTML(content);
    }

    @Inject
    Lang lang;
}
