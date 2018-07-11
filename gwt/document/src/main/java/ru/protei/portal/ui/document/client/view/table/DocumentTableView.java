package ru.protei.portal.ui.document.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.DownloadClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateUtils;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableActivity;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableView;

import java.util.LinkedList;
import java.util.List;

public class DocumentTableView extends Composite implements AbstractDocumentTableView {

    @Inject
    public void onInit(EditClickColumn<Document> editClickColumn, DownloadClickColumn<Document> downloadClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.downloadClickColumn = downloadClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractDocumentTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        downloadClickColumn.setDownloadHandler(activity);

        columns.forEach(col -> {
            col.setHandler(activity);
            col.setColumnProvider(columnProvider);
        });

        table.setLoadHandler(activity);
        table.setPagerListener(activity);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HTMLPanel getFilterContainer() {
        return filterContainer;
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void setRecordCount(int count) {
        table.setTotalRecords(count);
    }

    @Override
    public int getPageSize() {
        return table.getPageSize();
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }


    private void initTable() {
        editClickColumn.setPrivilege(En_Privilege.DOCUMENT_EDIT);

        columns.add(id);
        columns.add(decimalNumber);
        columns.add(project);

        table.addColumn(id.header, id.values);
        table.addColumn(downloadClickColumn.header, downloadClickColumn.values);
        table.addColumn(decimalNumber.header, decimalNumber.values);
        table.addColumn(project.header, project.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<Document> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider<Document> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Document> editClickColumn;
    DownloadClickColumn<Document> downloadClickColumn;
    List<ClickColumn<Document>> columns = new LinkedList<>();

    AbstractDocumentTableActivity activity;


    private final ClickColumn<Document> id = new ClickColumn<Document>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.documentIdColumnHeader());
        }

        @Override
        public void fillColumnValue(Element cell, Document value) {
            cell.setInnerText(value.getId().toString());
        }
    };

    private final ClickColumn<Document> decimalNumber = new ClickColumn<Document>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.designation());
            columnHeader.addClassName("document-number-column");
        }

        @Override
        public void fillColumnValue(Element cell, Document value) {
            String html = "";

            if (!StringUtils.isEmpty(value.getDecimalNumber())) {
                html += "<div class=\"decimal-number\">" + value.getDecimalNumber() + "</div> ";
            }

            html += "<div class=\"document-name\">\"" + value.getName() + "\"</div><br/>";
            html += "<b>" + value.getType().getName() + " " + DateUtils.getYearFromDate(value.getCreated()) + "</b>";
            cell.setInnerHTML(html);
        }
    };

    private final ClickColumn<Document> project = new ClickColumn<Document>() {
        @Override
        protected void fillColumnHeader(Element element) {
            element.setInnerText(lang.equipmentProject());
        }

        @Override
        public void fillColumnValue(Element cell, Document value) {
            ProjectInfo project = value.getProjectInfo();
            if (project != null)
                cell.setInnerText(project.getName());
        }
    };


    private static DocumentTableViewUiBinder ourUiBinder = GWT.create(DocumentTableViewUiBinder.class);

    interface DocumentTableViewUiBinder extends UiBinder<HTMLPanel, DocumentTableView> {
    }
}
