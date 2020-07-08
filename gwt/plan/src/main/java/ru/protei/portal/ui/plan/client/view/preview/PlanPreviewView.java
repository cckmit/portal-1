package ru.protei.portal.ui.plan.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.columns.ActionIconClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.plan.client.activity.preview.AbstractPlanPreviewActivity;
import ru.protei.portal.ui.plan.client.activity.preview.AbstractPlanPreviewView;
import ru.protei.portal.ui.plan.client.view.columns.IssueColumn;
import ru.protei.portal.ui.plan.client.view.edit.tables.PlannedIssuesTableView;

import java.util.List;

public class PlanPreviewView extends Composite implements AbstractPlanPreviewView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
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

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void putRecords(List<CaseShortView> list) {
        list.forEach(table::addRow);
    }

    private void initTable() {
        ClickColumnProvider<CaseShortView> issuesColumnProvider = new ClickColumnProvider<>();

        IssueColumn number = new IssueColumn(lang);
        table.addColumn(number.header, number.values);
        number.setHandler(value -> activity.onItemClicked(value));
        number.setColumnProvider(issuesColumnProvider);
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
    SpanElement period;
    @UiField
    TableWidget<CaseShortView> table;


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