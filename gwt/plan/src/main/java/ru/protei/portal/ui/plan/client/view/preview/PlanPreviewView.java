package ru.protei.portal.ui.plan.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.plan.client.activity.preview.AbstractPlanPreviewActivity;
import ru.protei.portal.ui.plan.client.activity.preview.AbstractPlanPreviewView;

public class PlanPreviewView extends Composite implements AbstractPlanPreviewView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractPlanPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setHeader(String value) { this.header.setText( value ); }

    @Override
    public void setName(String value) { this.name.setInnerText( value ); }

    @Override
    public void setCreatedBy(String value) { this.createdBy.setInnerHTML( value ); }

    @Override
    public void setPeriod(String value) { this.period.setInnerHTML( value ); }

    @Override
    public void setIssues(String value) { this.issues.setInnerHTML( value ); }

    @Override
    public void showFullScreen(boolean isFullScreen){
        backButtonContainer.setVisible(isFullScreen);
        rootWrapper.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @UiHandler( "header" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler( "backButton" )
    public void onGoToProjectClicked ( ClickEvent event) {
        if ( activity != null ) {
            activity.onGoToPlansClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        header.ensureDebugId(DebugIds.PLAN_PREVIEW.FULL_SCREEN_BUTTON);
        header.ensureDebugId(DebugIds.PLAN_PREVIEW.TITLE_LABEL);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PLAN_PREVIEW.DATE_CREATED_LABEL);
        name.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PLAN_PREVIEW.NAME_LABEL);
        period.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PLAN_PREVIEW.PERIOD_LABEL);

    }

    @UiField
    Button backButton;
    @UiField
    Element createdBy;
    @UiField
    Anchor header;
    @UiField
    DivElement period;
    @UiField
    DivElement issues;

    @UiField
    Lang lang;
    @UiField
    HeadingElement name;
    @UiField
    HTMLPanel backButtonContainer;
    @UiField
    HTMLPanel rootWrapper;

    AbstractPlanPreviewActivity activity;

    interface PlanPreviewViewUiBinder extends UiBinder<HTMLPanel, PlanPreviewView> {}
    private static PlanPreviewViewUiBinder ourUiBinder = GWT.create(PlanPreviewViewUiBinder.class);
}