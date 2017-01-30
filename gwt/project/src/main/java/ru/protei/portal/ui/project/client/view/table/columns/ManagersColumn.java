package ru.protei.portal.ui.project.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;

/**
 * Колонка с информацией о проекте
 */
public class ManagersColumn extends ClickColumn< ProjectInfo > {

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "managers" );
        columnHeader.setInnerText( lang.projectManagers() );
    }

    @Override
    public void fillColumnValue( Element cell, ProjectInfo value ) {
        cell.addClassName( "managers" );

        StringBuilder content = new StringBuilder();
        List<PersonShortView> managers = value.getManagers();
        if ( managers != null ) {
            content.append( "<b>" );
            for ( PersonShortView manager : managers ) {
                content.append( manager.getDisplayShortName() ).append( "<br/>" );
            }
            content.append( "</b>" );
        }

        PersonShortView headManager = value.getHeadManager();
        if ( headManager != null ) {
            content.append( headManager.getDisplayShortName() );
        }

        cell.setInnerHTML( content.toString() );
    }

    @Inject
    Lang lang;
}
