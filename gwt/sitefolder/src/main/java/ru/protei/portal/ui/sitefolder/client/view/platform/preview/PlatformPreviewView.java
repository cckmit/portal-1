package ru.protei.portal.ui.sitefolder.client.view.platform.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.collapse.CollapsablePanel;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractPlatformPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractPlatformPreviewView;

public class PlatformPreviewView extends Composite implements AbstractPlatformPreviewView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        attachmentContainer.setHiddenControls(true);
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
    public void setManager(String value) {
        manager.setInnerText(value);
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

    @Override
    public HasWidgets serversContainer() {
        return serversContainer;
    }

    @Override
    public HasAttachments attachmentsContainer() {
        return attachmentContainer;
    }

    @UiHandler("openServersButton")
    public void openButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenServersClicked();
        }
    }

    @UiField
    SpanElement name;
    @UiField
    SpanElement company;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement parameters;
    @UiField
    SpanElement comment;
    @UiField
    CollapsablePanel contactsContainer;
    @UiField
    CollapsablePanel serversContainer;
    @UiField
    Button openServersButton;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;

    @Inject
    FixedPositioner positioner;

    private AbstractPlatformPreviewActivity activity;

    interface SiteFolderPreviewViewUiBinder extends UiBinder<HTMLPanel, PlatformPreviewView> {}
    private static SiteFolderPreviewViewUiBinder ourUiBinder = GWT.create(SiteFolderPreviewViewUiBinder.class);
}
