package ru.protei.portal.ui.issueassignment.client.view.desk.rowadd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowadd.AbstractDeskRowAddView;

public class DeskRowAddView extends Composite implements AbstractDeskRowAddView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @UiHandler("button")
    public void buttonClick(ClickEvent event) {
        if (handler != null) {
            handler.onAdd();
        }
    }

    @UiField
    Button button;

    private Handler handler;

    interface DeskRowAddViewBinder extends UiBinder<HTMLPanel, DeskRowAddView> {}
    private static DeskRowAddViewBinder ourUiBinder = GWT.create(DeskRowAddViewBinder.class);
}
