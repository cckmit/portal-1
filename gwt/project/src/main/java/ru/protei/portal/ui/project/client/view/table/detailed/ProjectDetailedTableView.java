package ru.protei.portal.ui.project.client.view.table.detailed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.project.client.activity.table.detailed.AbstractProjectDetailedTableActivity;
import ru.protei.portal.ui.project.client.activity.table.detailed.AbstractProjectDetailedTableView;

import java.util.Date;
import java.util.List;

public class ProjectDetailedTableView extends Composite implements AbstractProjectDetailedTableView {

    public ProjectDetailedTableView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractProjectDetailedTableActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void addRecords( List< ProjectInfo > projects ) {
        projects.forEach( project -> table.addRow( project ) );
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    private void initTable () {

        StaticColumn<ProjectInfo> created = new StaticColumn<ProjectInfo>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("created");
                columnHeader.setInnerText(lang.issueReportsInfo());
            }

            @Override
            public void fillColumnValue(Element cell, ProjectInfo value) {
                cell.addClassName("created");
                Date created = value == null ? null : value.getCreated();
                cell.setInnerText(created == null ? "" : DateFormatter.formatDateTime(created));
            }
        };

        StaticColumn<ProjectInfo> name = new StaticColumn<ProjectInfo>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("name");
                columnHeader.setInnerText(lang.issueReportsInfo());
            }

            @Override
            public void fillColumnValue(Element cell, ProjectInfo value) {
                cell.addClassName("name");
                cell.setInnerText(value.getName());
            }
        };
    }

    @UiField
    TableWidget table;

    @Inject
    @UiField
    Lang lang;

    AbstractProjectDetailedTableActivity activity;

    private static ProjectDetailedTableViewUiBinder ourUiBinder = GWT.create( ProjectDetailedTableViewUiBinder.class );
    interface ProjectDetailedTableViewUiBinder extends UiBinder< HTMLPanel, ProjectDetailedTableView > {}
}