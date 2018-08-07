package ru.protei.portal.ui.project.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
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

        List<PersonProjectMemberView> team = value.getTeam();
        if (team != null) {
            content.append("<b>");
            team.stream()
                    .map(ppm -> roleTypeLang.getName(ppm.getRole()) + ": " + ppm.getDisplayShortName())
                    .forEach(s -> content
                            .append(s)
                            .append("<br/>")
                    );
            content.append("</b>");
        }

        cell.setInnerHTML( content.toString() );
    }

    @Inject
    Lang lang;
    @Inject
    En_PersonRoleTypeLang roleTypeLang;
}
