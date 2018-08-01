package ru.protei.portal.ui.sitefolder.client.view.platform.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractPlatformPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractPlatformPreviewView;

public class PlatformPreviewView extends Composite implements AbstractPlatformPreviewView {

    public PlatformPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractPlatformPreviewActivity activity) {
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
    public void setCompany(String value) {
        company.setInnerText(value);
    }

    @Override
    public void setParameters(String value) {
        parameters.setInnerText(value);
    }

    @Override
    public void setComment(String value) {
        comment.setInnerText(value);
    }

    @Override
    public HasWidgets contactsContainer() {
        return contactsContainer;
    }

    @UiField
    SpanElement name;
    @UiField
    SpanElement company;
    @UiField
    SpanElement parameters;
    @UiField
    SpanElement comment;
    @UiField
    HTMLPanel contactsContainer;

    @Inject
    FixedPositioner positioner;

    private AbstractPlatformPreviewActivity activity;

    interface SiteFolderPreviewViewUiBinder extends UiBinder<HTMLPanel, PlatformPreviewView> {}
    private static SiteFolderPreviewViewUiBinder ourUiBinder = GWT.create(SiteFolderPreviewViewUiBinder.class);
}
