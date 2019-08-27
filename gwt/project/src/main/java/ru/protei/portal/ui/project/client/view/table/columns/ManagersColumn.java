package ru.protei.portal.ui.project.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;

/**
 * Колонка со списком менеджеров проекта
 */
public class ManagersColumn extends ClickColumn< ProjectInfo > {

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "managers" );
        columnHeader.setInnerText( lang.projectTeam() );
    }

    @Override
    public void fillColumnValue( Element cell, ProjectInfo value ) {
        cell.addClassName( "managers" );

        StringBuilder content = new StringBuilder();

        List<PersonProjectMemberView> team = value.getTeam();
        if (team != null) {
            PersonProjectMemberView leader = team.stream()
                    .filter(ppm -> En_DevUnitPersonRoleType.HEAD_MANAGER.equals(ppm.getRole()))
                    .findFirst()
                    .orElse(null);
            if (leader != null) {
                content.append(leader.getDisplayShortName());
                if (team.size() - 1 > 0) {
                    content.append(" +")
                            .append(team.size() - 1)
                            .append(" ")
                            .append(lang.membersCount());
                }
            } else if (team.size() > 0) {
                content.append(team.size())
                        .append(" ")
                        .append(lang.membersCount());
            }
        }

        cell.setInnerHTML( content.toString() );
    }

    @Inject
    Lang lang;
}
