package ru.protei.portal.ui.documentation.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.documentation.client.activity.table.AbstractDocumentationTableActivity;
import ru.protei.portal.ui.documentation.client.activity.table.AbstractDocumentationTableView;

import java.util.LinkedList;
import java.util.List;

public class DocumentationTableView extends Composite implements AbstractDocumentationTableView {

    @Inject
    public void onInit(EditClickColumn<Documentation> editClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractDocumentationTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        columns.forEach(col -> {
            col.setHandler(activity);
            col.setColumnProvider(columnProvider);
        });
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
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void addRow(Documentation documentation) {
        table.addRow(documentation);
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }

    @Override
    public void updateRow(Documentation documentation) {
        table.updateRow(documentation);
    }

    private void initTable() {
        ClickColumn<Documentation> name = new ClickColumn<Documentation>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.equipmentNameBySpecification());
            }

            @Override
            public void fillColumnValue(Element cell, Documentation value) {
                cell.setInnerHTML(HTMLHelper.wrapDiv(
                        value.getName() +
                                "<div><i><small><i class='fa fa-file-o m-r-5'></i>" +
                                value.getName() +
                                "</small></i></div>"
                ));
            }
        };
        columns.add(name);

        ClickColumn<Documentation> decimalNumber = new ClickColumn<Documentation>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.designation());
                columnHeader.addClassName("documentation-number-column");
            }

            @Override
            public void fillColumnValue(Element cell, Documentation value) {
                if (value.getDecimalNumber() == null) {
                    return;
                }

                Element numElem = DOM.createDiv();
                numElem.setInnerHTML(value.getDecimalNumber().toString());
                if (value.getDecimalNumber().isReserve()) {
                    Element isReserveEl = DOM.createElement("i");
                    isReserveEl.addClassName("fa fa-flag text-danger m-l-10");
                    numElem.appendChild(isReserveEl);
                }
                cell.appendChild(numElem);

            }
        };
        columns.add(decimalNumber);

        ClickColumn<Documentation> project = new ClickColumn<Documentation>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.equipmentProject());
            }

            @Override
            public void fillColumnValue(Element cell, Documentation value) {
                String html = "";
                if (value.getManagerShortName() != null && value.getProject() != null) {
                    html = value.getProject() + "<div><i><small><i class='fa fa-user-o m-r-5'></i>" + value.getManagerShortName() + "</small></i></div>";
                }
                cell.setInnerHTML(HTMLHelper.wrapDiv(html));
            }
        };
        columns.add(project);

        ClickColumn<Documentation> annotation = new ClickColumn<Documentation>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.annotation());
            }

            @Override
            public void fillColumnValue(Element cell, Documentation value) {
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
    InfiniteTableWidget<Documentation> table;

    @UiField
    HTMLPanel tableContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider<Documentation> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Documentation> editClickColumn;
    List<ClickColumn<Documentation>> columns = new LinkedList<>();

    AbstractDocumentationTableActivity activity;

    private static DocumentationTableViewUiBinder ourUiBinder = GWT.create(DocumentationTableViewUiBinder.class);

    interface DocumentationTableViewUiBinder extends UiBinder<HTMLPanel, DocumentationTableView> {
    }
}
