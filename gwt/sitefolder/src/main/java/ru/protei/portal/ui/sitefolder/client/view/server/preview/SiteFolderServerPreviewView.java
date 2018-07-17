package ru.protei.portal.ui.sitefolder.client.view.server.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.AbstractSiteFolderServerPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.preview.AbstractSiteFolderServerPreviewView;

public class SiteFolderServerPreviewView extends Composite implements AbstractSiteFolderServerPreviewView {

    public SiteFolderServerPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractSiteFolderServerPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setName(String value) {
        name.setInnerText(value);
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
    public void setComment(String value) {
        comment.setInnerText(value);
    }

    @UiField
    SpanElement name;
    @UiField
    SpanElement platform;
    @UiField
    SpanElement ip;
    @UiField
    SpanElement parameters;
    @UiField
    SpanElement comment;

    @Inject
    FixedPositioner positioner;

    private AbstractSiteFolderServerPreviewActivity activity;

    interface SiteFolderServerPreviewViewUiBinder extends UiBinder<HTMLPanel, SiteFolderServerPreviewView> {}
    private static SiteFolderServerPreviewViewUiBinder ourUiBinder = GWT.create(SiteFolderServerPreviewViewUiBinder.class);
}
