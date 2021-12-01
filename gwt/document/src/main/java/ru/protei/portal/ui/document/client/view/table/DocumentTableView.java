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
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableActivity;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableView;

import java.util.LinkedList;
import java.util.List;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class DocumentTableView extends Composite implements AbstractDocumentTableView {

    @Inject
    public void onInit(EditClickColumn<Document> editClickColumn, ArchiveClickColumn<Document> archiveClickColumn,
                       RemoveClickColumn<Document> removeClickColumn
    ) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.archiveClickColumn = archiveClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractDocumentTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        archiveClickColumn.setArchiveHandler(activity);
        archiveClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);

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
        columnProvider.removeSelection();
    }

    private void initTable() {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_EDIT) && !v.isDeprecatedUnit() );
        archiveClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_EDIT) );
        archiveClickColumn.setArchiveFilter(Document::isDeprecatedUnit);
        archiveClickColumn.setIconProvider(new ArchiveClickColumn.IconProvider() {
            public String addToArchive() { return "far fa-hdd"; }
            public String removeFromArchive() { return "fa fa-history"; }
        });
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_REMOVE) && !v.isDeprecatedUnit());

        columns.add(approve);
        columns.add(type);
        columns.add(name);
        columns.add(decimalNumber);
        columns.add(company);

        table.addColumn(approve.header, approve.values);
        table.addColumn(type.header, type.values);
        table.addColumn(name.header, name.values);
        table.addColumn(decimalNumber.header, decimalNumber.values);
        table.addColumn(project.header, project.values);
        table.addColumn(company.header, company.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(archiveClickColumn.header, archiveClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    private final ClickColumn<Document> approve = new ClickColumn<Document>() {
        @Override
        protected String getColumnClassName() { return "document-approve-column"; }
        @Override
        protected void fillColumnHeader(Element columnHeader) {}
        @Override
        protected void fillColumnValue(Element cell, Document value) {
            String icon = value.getApproved() ? UiConstants.Icons.APPROVED : UiConstants.Icons.NOT_APPROVED;
            cell.setInnerHTML("<i class='fa fa-lg " + icon + "'></i>");
            if (value.isDeprecatedUnit()) {
                cell.addClassName("deprecated-entity");
            }
        }
    };

    private final ClickColumn<Document> name = new ClickColumn<Document>() {
        @Override
        protected String getColumnClassName() { return "document-name-column"; }
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.documentName());
        }
        @Override
        public void fillColumnValue(Element cell, Document value) {
            StringBuilder html = new StringBuilder();

            if (value.isDeprecatedUnit()) {
                html
                        .append("<div class =\"document-name text-overflow-dynamic-container\">")
                        .append("<i class=\"fa fa-lock m-r-5\" id=\"" + DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.TABLE.LOCK_ICON + "\"></i> ")
                        .append("<span class=\"text-overflow-dynamic-ellipsis m-l-20\">" + value.getName() + "</span>")
                        .append("</div>");
            } else {
                html
                        .append( "<div class=\"document-name text-overflow-dynamic-container\">")
                        .append("<span class=\"text-overflow-dynamic-ellipsis\">" + value.getName() + "</span>")
                        .append("</div>");
            }

            cell.setInnerHTML(sanitizeHtml(html.toString()));

            if (value.isDeprecatedUnit()) {
                cell.addClassName("deprecated-entity");
            }
        }
    };

    private final ClickColumn<Document> decimalNumber = new ClickColumn<Document>() {
        @Override
        protected String getColumnClassName() { return "document-number-column"; }
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.designation());
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
        protected String getColumnClassName() { return "document-project-column"; }
        @Override
        protected void fillColumnHeader(Element element) {
            element.setInnerText(lang.equipmentProject());
        }
        @Override
        public void fillColumnValue(Element cell, Document value) {
            StringBuilder html = new StringBuilder();
            if (policyService.hasPrivilegeFor(En_Privilege.PROJECT_VIEW)) {
                html
                        .append("<a href=\"#\" title=\"")
                        .append(value.getProjectLocation() == null ? "" : value.getProjectLocation())
                        .append("\" class=\"text-overflow-dynamic-container\">")
                        .append("<span class=\"text-overflow-dynamic-ellipsis\">")
                        .append(StringUtils.emptyIfNull(value.getProjectName()))
                        .append("</span></a>");
            } else {
                html
                        .append("<div title=\"")
                        .append(value.getProjectLocation() == null ? "" : value.getProjectLocation())
                        .append("\" class=\"text-overflow-dynamic-container\">")
                        .append("<span class=\"text-overflow-dynamic-ellipsis\">")
                        .append(StringUtils.emptyIfNull(value.getProjectName()))
                        .append("</span></div>");
            }

            cell.setInnerHTML(html.toString());

            if (value.isDeprecatedUnit()) {
                cell.addClassName("deprecated-entity");
            }
        }
    };

    private final ClickColumn<Document> company = new ClickColumn<Document>() {
        @Override
        protected String getColumnClassName() { return "document-company-column"; }
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.company());
        }
        @Override
        public void fillColumnValue(Element cell, Document value) {
            StringBuilder html = new StringBuilder();

            if (value.getContragentName() != null) {
                html
                        .append("<div class=\"company text-overflow-dynamic-container\">")
                        .append("<span class=\"text-overflow-dynamic-ellipsis\">" + value.getContragentName() + "</span>")
                        .append("</div> ");
            }

            cell.setInnerHTML(html.toString());
        }
    };

    private final ClickColumn<Document> type = new ClickColumn<Document>() {
        @Override
        protected String getColumnClassName() { return "document-type-column"; }
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.documentTypeShort());
        }
        @Override
        public void fillColumnValue(Element cell, Document value) {
            StringBuilder html = new StringBuilder();

            if (value.getType() != null) {
                html
                        .append("<div class=\"type\">")
                        .append(value.getType().getShortName())
                        .append("</div> ");
            }

            cell.setInnerHTML(html.toString());
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

    @Inject
    PolicyService policyService;

    ClickColumnProvider<Document> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Document> editClickColumn;
    ArchiveClickColumn<Document> archiveClickColumn;
    RemoveClickColumn<Document> removeClickColumn;
    List<ClickColumn<Document>> columns = new LinkedList<>();

    AbstractDocumentTableActivity activity;
    private static DocumentTableViewUiBinder ourUiBinder = GWT.create(DocumentTableViewUiBinder.class);
    interface DocumentTableViewUiBinder extends UiBinder<HTMLPanel, DocumentTableView> {

    }
}
