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
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableActivity;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableView;

import java.util.LinkedList;
import java.util.List;

public class DocumentTableView extends Composite implements AbstractDocumentTableView {

    @Inject
    public void onInit(EditClickColumn<Document> editClickColumn, DownloadClickColumn<Document> downloadClickColumn,
                       ArchiveClickColumn<Document> archiveClickColumn, DocumentNameColumn<Document> documentNameColumn,
                       RemoveClickColumn<Document> removeClickColumn
    ) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.downloadClickColumn = downloadClickColumn;
        this.archiveClickColumn = archiveClickColumn;
        this.removeClickColumn = removeClickColumn;
        this.documentNameColumn = documentNameColumn;

        editClickColumn.setArchivedCheckFunction(Document::isDeprecatedUnit);
        archiveClickColumn.setArchivedCheckFunction(Document::isDeprecatedUnit);
        removeClickColumn.setArchivedCheckFunction(Document::isDeprecatedUnit);
        downloadClickColumn.setArchivedCheckFunction(Document::isDeprecatedUnit);
        initTable();
    }

    @Override
    public void setActivity(AbstractDocumentTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        downloadClickColumn.setDownloadHandler(activity);

        archiveClickColumn.setArchiveHandler(activity);
        archiveClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);

        documentNameColumn.setColumnProvider(columnProvider);

        project.setActionHandler(activity::onProjectColumnClicked);

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
    public HasWidgets getPagerContainer() {
        return pagerContainer;
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
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
    }

    private void initTable() {
        editClickColumn.setPrivilege(En_Privilege.DOCUMENT_EDIT);
        downloadClickColumn.setDownloadCustomImage("./images/pdficon.png");
        archiveClickColumn.setPrivilege(En_Privilege.DOCUMENT_EDIT);
        removeClickColumn.setPrivilege(En_Privilege.DOCUMENT_REMOVE);
        downloadClickColumn.setPrivilege(En_Privilege.DOCUMENT_EDIT);

        columns.add(id);
        columns.add(documentNameColumn);
        columns.add(decimalNumber);

        table.addColumn(id.header, id.values);
        table.addColumn(downloadClickColumn.header, downloadClickColumn.values);
        table.addColumn(documentNameColumn.header, documentNameColumn.values);
        table.addColumn(decimalNumber.header, decimalNumber.values);
        table.addColumn(project.header, project.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(archiveClickColumn.header, archiveClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    private final ClickColumn<Document> id = new ClickColumn<Document>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.documentIdColumnHeader());
        }

        @Override
        public void fillColumnValue(Element cell, Document value) {
            cell.setInnerText(value.getId().toString());
            if (value.isDeprecatedUnit()) {
                cell.addClassName("deprecated-entity");
            }
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
            StringBuilder html = new StringBuilder();

            if (!StringUtils.isEmpty(value.getDecimalNumber())) {
                html
                        .append("<div class=\"decimal-number\">")
                        .append(value.getDecimalNumber())
                        .append("</div> ");
            }

            cell.setInnerHTML(html.toString());

            if (value.isDeprecatedUnit()) {
                cell.addClassName("deprecated-entity");
            }
        }
    };

    private final ClickColumn<Document> project = new ClickColumn<Document>() {
        @Override
        protected void fillColumnHeader(Element element) {
            element.setInnerText(lang.equipmentProject());
        }

        @Override
        public void fillColumnValue(Element cell, Document value) {
            Project project = value.getProject();
            if (project == null) {
                return;
            }

            cell.setInnerHTML("<a href=\"#\">" + project.getName() + "</a>");

            if (value.isDeprecatedUnit()) {
                cell.addClassName("deprecated-entity");
            }
        }
    };


    @UiField
    InfiniteTableWidget<Document> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider<Document> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Document> editClickColumn;
    DownloadClickColumn<Document> downloadClickColumn;
    ArchiveClickColumn<Document> archiveClickColumn;
    RemoveClickColumn<Document> removeClickColumn;
    DocumentNameColumn<Document> documentNameColumn;
    List<ClickColumn<Document>> columns = new LinkedList<>();

    AbstractDocumentTableActivity activity;
    private static DocumentTableViewUiBinder ourUiBinder = GWT.create(DocumentTableViewUiBinder.class);
    interface DocumentTableViewUiBinder extends UiBinder<HTMLPanel, DocumentTableView> {

    }
}
