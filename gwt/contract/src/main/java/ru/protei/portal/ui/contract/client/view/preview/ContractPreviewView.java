package ru.protei.portal.ui.contract.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuelinks.list.IssueLinks;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewActivity;
import ru.protei.portal.ui.contract.client.activity.preview.AbstractContractPreviewView;

import java.util.Set;

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


    @UiField
    HTMLPanel commentContainer;
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    private AbstractContractPreviewActivity activity;

    private static PreviewViewUiBinder ourUiBinder = GWT.create( PreviewViewUiBinder.class );
    interface PreviewViewUiBinder extends UiBinder< HTMLPanel, ContractPreviewView> {}
}