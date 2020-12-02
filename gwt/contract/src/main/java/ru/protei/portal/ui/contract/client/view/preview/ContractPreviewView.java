package ru.protei.portal.ui.contract.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewActivity;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewView;
import ru.protei.portal.ui.contract.client.widget.contractspecification.previewitem.ContractSpecificationPreviewItem;

import java.util.List;

public class ContractPreviewView extends Composite implements AbstractContractPreviewView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public void setHeader(String value) {
        this.header.setText(value);
    }

    @Override
    public void setState(String value) {
        this.state.setSrc(value);
    }

    @Override
    public void setDateSigning(String value) {
        this.dateSigning.setInnerHTML(value);
    }

    @Override
    public void setDateValid(String value) {
        this.dateValid.setInnerHTML(value);
    }

    @Override
    public void setDescription(String value) {
        this.description.setInnerHTML(value);
    }

    @Override
    public void setDirection(String value) {
        this.direction.setInnerHTML(value);
    }

    @Override
    public void setContractor(String value) {
        this.contractor.setInnerHTML(value);
    }

    @Override
    public void setCurator(String value) {
        this.curator.setInnerHTML(value);
    }

    @Override
    public void setProjectManager(String value) {
        this.projectManager.setInnerHTML(value);
    }

    @Override
    public void setContractSignManager(String value) {
        this.contractSignManager.setInnerHTML(value);
    }

    @Override
    public void setDates(List<Widget> value) {
        this.dates.clear();
        value.forEach(widget -> this.dates.add(widget));
    }

    @Override
    public void setSpecifications(List<ContractSpecificationPreviewItem> value) {
        this.specifications.clear();
        value.forEach(item -> this.specifications.add(item));
    }

    @Override
    public void setOrganization(String value) {
        this.organization.setInnerHTML(value);
    }

    @Override
    public void setParentContract(String value) {
        this.contractParent.setInnerHTML(value);
    }

    @Override
    public void setChildContracts(String value) {
        this.contractChild.setInnerHTML(value);
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

    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentContainer;
    @UiField
    SpanElement dateSigning;
    @UiField
    SpanElement dateValid;
    @UiField
    SpanElement description;
    @UiField
    SpanElement direction;
    @UiField
    ImageElement state;
    @UiField
    SpanElement organization;
    @UiField
    SpanElement projectManager;
    @UiField
    SpanElement curator;
    @UiField
    SpanElement contractor;
    @UiField
    Anchor header;
    @UiField
    HTMLPanel dates;
    @UiField
    HTMLPanel specifications;
    @UiField
    SpanElement contractParent;
    @UiField
    SpanElement contractChild;
    @UiField
    Anchor project;
    @UiField
    HTMLPanel footerContainer;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    SpanElement contractSignManager;

    private AbstractContractPreviewActivity activity;

    private static PreviewViewUiBinder ourUiBinder = GWT.create( PreviewViewUiBinder.class );
    interface PreviewViewUiBinder extends UiBinder< HTMLPanel, ContractPreviewView> {}
}