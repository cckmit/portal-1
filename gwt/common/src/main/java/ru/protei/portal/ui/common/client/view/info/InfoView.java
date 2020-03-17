package ru.protei.portal.ui.common.client.view.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.activity.info.AbstractInfoActivity;
import ru.protei.portal.ui.common.client.activity.info.AbstractInfoView;
import ru.protei.portal.ui.common.client.activity.info.InfoActivity;

public class InfoView extends Composite implements AbstractInfoView {
    public InfoView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractInfoActivity activity) {
        this.activity = activity;
    }

    @UiField
    TableWidget<InfoActivity.JiraToCrmStatus> table;

    private AbstractInfoActivity activity;

    interface InfoViewUiBinder extends UiBinder<HTMLPanel, InfoView> {
    }
    private static InfoViewUiBinder ourUiBinder = GWT.create(InfoViewUiBinder.class);
}