package ru.protei.portal.ui.employee.client.view.topbrass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.employee.client.activity.topbrass.AbstractTopBrassActivity;
import ru.protei.portal.ui.employee.client.activity.topbrass.AbstractTopBrassView;

public class TopBrassView extends Composite implements AbstractTopBrassView {
    public TopBrassView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractTopBrassActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets topContainer() {
        return topContainer;
    }

    @Override
    public HasWidgets bottomContainer() {
        return bottomContainer;
    }

    @UiField
    HTMLPanel topContainer;

    @UiField
    HTMLPanel bottomContainer;

    private AbstractTopBrassActivity activity;

    interface TopBrassViewUiBinder extends UiBinder<HTMLPanel, TopBrassView> {}
    private static TopBrassViewUiBinder ourUiBinder = GWT.create(TopBrassViewUiBinder.class);
}