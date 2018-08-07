package ru.protei.portal.ui.project.client.view.widget.team.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.project.client.view.widget.team.AbstractTeamSelector;

public class TeamSelectorItem extends Composite implements AbstractTeamSelectorItem {

    public TeamSelectorItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractTeamSelector activity) {

    }

    @Override
    public void setModel(TeamSelectorItemModel itemModel) {

    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    private boolean isEnabled = true;

    interface TeamSelectorItemUiBinder extends UiBinder<HTMLPanel, TeamSelectorItem> {}
    private static TeamSelectorItemUiBinder ourUiBinder = GWT.create(TeamSelectorItemUiBinder.class);
}
