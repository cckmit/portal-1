package ru.protei.portal.ui.contract.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractSla;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.sla.SlaInput;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewActivity;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewView;

import java.util.List;

public class ContractPreviewView extends Composite implements AbstractContractPreviewView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractContractPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentContainer;
    }

    @Override
    public void setHeader(String value) {
        this.header.setText(value);
    }

    @Override
    public void setType(String value) {
        this.type.setInnerText(value);
    }

    @Override
    public void setState(String value) {
        this.state.setSrc(value);
    }

    @Override
    public void setDateSigning(String value) {
        this.dateSigning.setInnerText(value);
    }

    @Override
    public void setDateValid(String value) {
        this.dateValid.setInnerText(value);
    }

    @Override
    public void setDescription(String value) {
        this.description.setText(value);
    }

    @Override
    public void setDirection(String value) {
        this.direction.setInnerText(value);
    }

    @Override
    public void setContragent(String value) {
        this.contragent.setInnerText(value);
    }

    @Override
    public void setCurator(String value) {
        this.curator.setInnerText(value);
    }

    @Override
    public void setManager(String value) {
        this.manager.setInnerText(value);
    }

    @Override
    public void setDates(String value) {
        this.dates.setInnerText(value);
    }

    @Override
    public void setOrganization(String value) {
        this.organization.setInnerText(value);
    }

    @Override
    public void setParentContract(String value) {
        this.contractParent.setInnerText(value);
    }

    @Override
    public void setChildContracts(String value) {
        this.contractChild.setInnerText(value);
    }

    @Override
    public void setProject(String value, String link) {
        project.setText(value);
        project.setHref(link);
    }

    @Override
    public HasVisibility footerVisibility() {
        return footerContainer;
    }

    @Override
    public void isFullScreen(boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
        if (isFullScreen) {
            slaInput.getElement().replaceClassName("col-md-12", "col-md-6");
        } else {
            slaInput.getElement().replaceClassName("col-md-6", "col-md-12");
        }
    }

    @Override
    public HasValue<List<ContractSla>> slaInput() {
        return slaInput;
    }

    @Override
    public HasVisibility slaInputVisibility() {
        return slaContainer;
    }

    @UiHandler("header")
    public void onFullScreenClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onFullScreenClicked();
        }
    }

    @UiHandler("backButton")
    public void onGoToContractsClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onGoToContractsClicked();
        }
    }

    @UiHandler("slaInput")
    public void onSaveClicked(ClickEvent event) {
        activity.onSaveSlaClicked();
    }

    private void ensureDebugIds() {
        slaInput.setEnsureDebugId(DebugIds.CONTRACT.PREVIEW.SLA_INPUT);
        slaInput.setSaveButtonDebugId(DebugIds.CONTRACT.PREVIEW.SLA_SAVE_BUTTON);
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentContainer;
    @UiField
    Element type;
    @UiField
    SpanElement dateSigning;
    @UiField
    SpanElement dateValid;
    @UiField
    InlineLabel description;
    @UiField
    SpanElement direction;
    @UiField
    ImageElement state;
    @UiField
    SpanElement organization;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement curator;
    @UiField
    SpanElement contragent;
    @UiField
    Anchor header;
    @UiField
    LabelElement dates;
    @UiField
    SpanElement contractParent;
    @UiField
    SpanElement contractChild;
    @UiField
    Anchor project;
    @Inject
    @UiField(provided = true)
    SlaInput slaInput;
    @UiField
    HTMLPanel slaContainer;
    @UiField
    HTMLPanel footerContainer;
    @UiField
    HTMLPanel previewWrapperContainer;

    private AbstractContractPreviewActivity activity;

    private static PreviewViewUiBinder ourUiBinder = GWT.create( PreviewViewUiBinder.class );
    interface PreviewViewUiBinder extends UiBinder< HTMLPanel, ContractPreviewView> {}
}