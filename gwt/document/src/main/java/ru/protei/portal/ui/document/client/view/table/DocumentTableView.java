package ru.protei.portal.ui.document.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableActivity;
import ru.protei.portal.ui.document.client.activity.table.AbstractDocumentTableView;

import java.util.LinkedList;
import java.util.List;

public class DocumentTableView extends Composite implements AbstractDocumentTableView {

    @Inject
    public void onInit(EditClickColumn<Document> editClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractDocumentTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

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

        ClickColumn<Document> name = new ClickColumn<Document>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.equipmentNameBySpecification());
            }

            @Override
            public void fillColumnValue(Element cell, Document value) {
                cell.setInnerHTML(HTMLHelper.wrapDiv(
                        value.getName() +
                                "<div><i><small><i class='fa fa-clock-o m-r-5'></i>" +
                                DateFormatter.formatDateTime(value.getCreated()) +
                                "</small></i></div>"
                ));
            }
        };
        columns.add(name);

        ClickColumn<Document> decimalNumber = new ClickColumn<Document>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.designation());
                columnHeader.addClassName("document-number-column");
            }

            @Override
            public void fillColumnValue(Element cell, Document value) {
                String dNumber = value.getDecimalNumber();
                if (StringUtils.isEmpty(dNumber)) {
                    return;
                }

                Element numElem = DOM.createDiv();
                numElem.setClassName("equipment-number");
                numElem.setInnerHTML(dNumber);
                cell.appendChild(numElem);
            }
        };
        columns.add(decimalNumber);

        ClickColumn<Document> project = new ClickColumn<Document>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.equipmentProject());
            }

            @Override
            public void fillColumnValue(Element cell, Document value) {
                String html = "";
                ProjectInfo project = value.getProjectInfo();
                if (project != null) {
                    html = project.getName();
                    if (project.getHeadManager() != null) {
                        html += "<div><i><small><i class='fa fa-user-o m-r-5'></i>" +
                                project.getHeadManager().getDisplayShortName() +
                                "</small></i></div>";
                    }
                }
                cell.setInnerHTML(HTMLHelper.wrapDiv(html));
            }
        };
        columns.add(project);

        ClickColumn<Document> annotation = new ClickColumn<Document>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.annotation());
            }

            @Override
            public void fillColumnValue(Element cell, Document value) {
                if (value.getAnnotation() == null) {
                    return;
                }
                cell.setInnerHTML("<div><i><small>" + value.getAnnotation() + "</i></small></div>");
            }
        };

        columns.add(annotation);

        columns.forEach(col -> table.addColumn(col.header, col.values));
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
    List<ClickColumn<Document>> columns = new LinkedList<>();

    AbstractDocumentTableActivity activity;

    private static DocumentTableViewUiBinder ourUiBinder = GWT.create(DocumentTableViewUiBinder.class);

    interface DocumentTableViewUiBinder extends UiBinder<HTMLPanel, DocumentTableView> {
    }
}
