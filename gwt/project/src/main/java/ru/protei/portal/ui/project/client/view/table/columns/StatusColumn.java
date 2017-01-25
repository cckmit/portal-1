package ru.protei.portal.ui.project.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_RegionStateLang;

/**
 * Колонка со статусом проекта
 */
public class StatusColumn extends ClickColumn< ProjectInfo > {

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "status" );
    }

    @Override
    public void fillColumnValue( Element cell, ProjectInfo value ) {
        cell.addClassName( "status" );
        cell.setInnerHTML( "<i class='"+lang.getStateIcon( value.getState() )+" fa-2x"+"'></i>" );
    }

    @Inject
    En_RegionStateLang lang;
}
