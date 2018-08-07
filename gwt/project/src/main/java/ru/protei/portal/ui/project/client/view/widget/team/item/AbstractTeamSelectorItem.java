package ru.protei.portal.ui.project.client.view.widget.team.item;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.project.client.view.widget.team.AbstractTeamSelector;

public interface AbstractTeamSelectorItem extends IsWidget, HasEnabled {

    void setActivity(AbstractTeamSelector activity);

    void setModel(TeamSelectorItemModel itemModel);
}