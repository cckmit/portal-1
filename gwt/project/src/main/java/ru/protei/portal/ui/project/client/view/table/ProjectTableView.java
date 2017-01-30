package ru.protei.portal.ui.project.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableActivity;
import ru.protei.portal.ui.project.client.activity.table.AbstractProjectTableView;
import ru.protei.portal.ui.project.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.project.client.view.table.columns.ManagersColumn;
import ru.protei.portal.ui.project.client.view.table.columns.NumberColumn;
import ru.protei.portal.ui.project.client.view.table.columns.StatusColumn;


/**
 * Представление таблицы проектов
 */
public class ProjectTableView extends Composite implements AbstractProjectTableView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void setActivity( AbstractProjectTableActivity activity ) {
        this.activity = activity;
        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );
        number.setHandler( activity );
        number.setColumnProvider( columnProvider );
        status.setHandler( activity );
        status.setColumnProvider( columnProvider );
        info.setHandler( activity );
        info.setColumnProvider( columnProvider );
        manager.setHandler( activity );
        manager.setColumnProvider( columnProvider );
    }
    
    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }


    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void addRow( ProjectInfo row ) {
        table.addRow( row );
    }

    @Override
    public void addSeparator( String text ) {
        Element elem = DOM.createDiv();
        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
        safeHtmlBuilder.appendHtmlConstant( "<b>" ).appendEscapedLines( text ).appendHtmlConstant( "</b>" );
        elem.setInnerSafeHtml( safeHtmlBuilder.toSafeHtml() );
        table.addCustomRow( elem, "region", null );
    }

    @Override
    public void updateRow( ProjectInfo project ) {
        table.updateRow( project );
    }

    private void initTable () {
        editClickColumn = new EditClickColumn< ProjectInfo >( lang ) {};

        table.addColumn( status.header, status.values );
        table.addColumn( number.header, number.values );
        table.addColumn( info.header, info.values );
        table.addColumn( manager.header, manager.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
    }

    @UiField
    TableWidget<ProjectInfo> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider<ProjectInfo> columnProvider = new ClickColumnProvider<>();
    EditClickColumn< ProjectInfo > editClickColumn;
    @Inject
    NumberColumn number;
    @Inject
    StatusColumn status;
    @Inject
    ManagersColumn manager;
    @Inject
    InfoColumn info;

    AbstractProjectTableActivity activity;

    private static IssueTableViewUiBinder ourUiBinder = GWT.create( IssueTableViewUiBinder.class );
    interface IssueTableViewUiBinder extends UiBinder< HTMLPanel, ProjectTableView> {}
}