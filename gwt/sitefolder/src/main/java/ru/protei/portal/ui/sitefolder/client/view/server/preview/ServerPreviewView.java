package ru.protei.portal.ui.sitefolder.client.view.server.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.AbstractServerPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.AbstractServerPreviewView;

public class ServerPreviewView extends Composite implements AbstractServerPreviewView {

    public ServerPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractServerPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String value) {
        name.setText(value);
    }

    @Override
    public void setPlatform(String value) {
        platform.setInnerText(value);
    }

    @Override
    public void setIp(String value) {
        ip.setInnerText(value);
    }

    @Override
    public void setParameters(String value) {
        parameters.setInnerText(value);
    }

    @Override
    public void setServerGroup(String value) {
        serverGroup.setInnerText(value);
    }

    @Override
    public void setComment(String value) {
        comment.setText(value);
    }

    @UiHandler("openAppsButton")
    public void openButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenAppsClicked();
        }
    }

    @UiField
    InlineLabel name;
    @UiField
    SpanElement platform;
    @UiField
    SpanElement ip;
    @UiField
    SpanElement parameters;
    @UiField
    SpanElement serverGroup;
    @UiField
    Label comment;
    @UiField
    Button openAppsButton;

    private AbstractServerPreviewActivity activity;

    interface SiteFolderServerPreviewViewUiBinder extends UiBinder<HTMLPanel, ServerPreviewView> {}
    private static SiteFolderServerPreviewViewUiBinder ourUiBinder = GWT.create(SiteFolderServerPreviewViewUiBinder.class);
}
