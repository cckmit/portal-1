package ru.protei.portal.ui.sitefolder.client.view.app.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PathInfo;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.AbstractApplicationPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.preview.AbstractApplicationPreviewView;

import java.util.stream.Collectors;

public class ApplicationPreviewView extends Composite implements AbstractApplicationPreviewView {

    public ApplicationPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractApplicationPreviewActivity activity) {
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
        name.setText(value);
    }

    @Override
    public void setComponent(String value) {
        component.setInnerText(value);
    }

    @Override
    public void setServer(String value) {
        server.setInnerText(value);
    }

    @Override
    public void setComment(String value) {
        comment.setText(value);
    }

    @Override
    public void setPaths(PathInfo value) {
        paths.removeAllChildren();
        value.getPaths().stream()
                .map(pathItem -> pathItem.getPath() + (HelperFunc.isNotEmpty(pathItem.getDesc()) ? " (" + pathItem.getDesc() + ")" : ""))
                .forEach(text -> {
                    Element p = DOM.createElement("p");
                    p.setInnerText(text);
                    paths.appendChild(p);
                });
    }

    @UiField
    InlineLabel name;
    @UiField
    Label comment;
    @UiField
    SpanElement paths;
    @UiField
    SpanElement server;
    @UiField
    SpanElement component;

    @Inject
    FixedPositioner positioner;

    private AbstractApplicationPreviewActivity activity;

    interface SiteFolderAppPreviewViewUiBinder extends UiBinder<HTMLPanel, ApplicationPreviewView> {}
    private static SiteFolderAppPreviewViewUiBinder ourUiBinder = GWT.create(SiteFolderAppPreviewViewUiBinder.class);
}

