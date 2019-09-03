package ru.protei.portal.ui.contract.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewActivity;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewView;

public class ContractPreviewView extends Composite implements AbstractContractPreviewView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch( this, FixedPositioner.NAVBAR_TOP_OFFSET );
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore( this );
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
        this.header.setInnerText(value);
    }

    @Override
    public void setType(String value) {
        this.type.setInnerText(value);
    }

    @Override
    public void setState(String value) {
        this.state.setInnerText(value);
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
        this.description.setInnerText(value);
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
    public void setProject(String value) {
        project.setInnerText(value);
        toProjectLink.setVisible(!value.isEmpty());
    }

    @UiHandler("toProjectLink")
    public void onProjectLinkClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onProjectLinkClicked();
        }
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel commentContainer;
    @UiField
    SpanElement type;
    @UiField
    SpanElement dateSigning;
    @UiField
    SpanElement dateValid;
    @UiField
    SpanElement description;
    @UiField
    SpanElement direction;
    @UiField
    SpanElement state;
    @UiField
    SpanElement organization;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement curator;
    @UiField
    SpanElement contragent;
    @UiField
    Element header;
    @UiField
    LabelElement dates;
    @UiField
    SpanElement contractParent;
    @UiField
    SpanElement contractChild;
    @UiField
    SpanElement project;
    @UiField
    Anchor toProjectLink;

    @Inject
    private FixedPositioner positioner;

    private AbstractContractPreviewActivity activity;

    private static PreviewViewUiBinder ourUiBinder = GWT.create( PreviewViewUiBinder.class );
    interface PreviewViewUiBinder extends UiBinder< HTMLPanel, ContractPreviewView> {}
}