package ru.protei.portal.ui.sitefolder.client.view.app.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.PathInfo;
import ru.protei.portal.core.model.struct.PathItem;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.AbstractSiteFolderAppPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.AbstractSiteFolderAppPreviewView;

import java.util.stream.Collectors;

public class SiteFolderAppPreviewView extends Composite implements AbstractSiteFolderAppPreviewView {

    public SiteFolderAppPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractSiteFolderAppPreviewActivity activity) {
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
    public void setServer(String value) {
        server.setInnerText(value);
    }

    @Override
    public void setComment(String value) {
        comment.setInnerText(value);
    }

    @Override
    public void setPaths(PathInfo value) {
        paths.setInnerHTML(value.getPaths().stream()
                .map(pathItem -> pathItem.getDesc() + " (" + pathItem.getPath() + ")")
                .collect(Collectors.joining("<br>"))
        );
    }

    @UiField
    SpanElement name;
    @UiField
    SpanElement comment;
    @UiField
    SpanElement paths;
    @UiField
    SpanElement server;

    @Inject
    FixedPositioner positioner;

    private AbstractSiteFolderAppPreviewActivity activity;

    interface SiteFolderAppPreviewViewUiBinder extends UiBinder<HTMLPanel, SiteFolderAppPreviewView> {}
    private static SiteFolderAppPreviewViewUiBinder ourUiBinder = GWT.create(SiteFolderAppPreviewViewUiBinder.class);
}

