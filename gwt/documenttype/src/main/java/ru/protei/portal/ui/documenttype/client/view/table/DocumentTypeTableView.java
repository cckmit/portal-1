package ru.protei.portal.ui.documenttype.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.En_DocumentCategoryLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.documenttype.client.activity.table.AbstractDocumentTypeTableActivity;
import ru.protei.portal.ui.documenttype.client.activity.table.AbstractDocumentTypeTableView;

import java.util.Collection;
import java.util.LinkedList;

public class DocumentTypeTableView extends Composite implements AbstractDocumentTypeTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractDocumentTypeTableActivity activity) {
        this.activity = activity;

        editClickColumn.setEditHandler(activity);
        columns.forEach(c -> {
            c.setHandler(activity);
            c.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void addRow(DocumentType row) {
        table.addRow(row);
    }

    @Override
    public void updateRow(DocumentType doctype) {
        table.updateRow(doctype);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
    }

    @Override
    public HTMLPanel getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HTMLPanel getPreviewContainer() {
        return previewContainer;
    }

    private void initTable() {
        editClickColumn.setPrivilege( En_Privilege.DOCUMENT_TYPE_EDIT);

        columns.add(name);
        columns.add(shortName);
        columns.add(category);
        columns.add(editClickColumn);

        columns.forEach(c -> table.addColumn(c.header, c.values));
    }

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    @UiField
    TableWidget<DocumentType> table;

    private ClickColumn<DocumentType> name = new ClickColumn<DocumentType>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.documentTypeName());
        }

        @Override
        public void fillColumnValue(Element cell, DocumentType value) {
            cell.setInnerText(value.getName());
        }
    };

    private ClickColumn<DocumentType> shortName = new ClickColumn<DocumentType>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.documentTypeShortName());
        }

        @Override
        public void fillColumnValue(Element cell, DocumentType value) {
            cell.setInnerText(value.getShortName());
        }
    };

    private ClickColumn<DocumentType> category = new ClickColumn<DocumentType>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.documentCategory());
        }

        @Override
        public void fillColumnValue(Element cell, DocumentType value) {
            cell.setInnerText(documentCategoryLang.getDocumentCategoryName(value.getDocumentCategory()));
        }
    };

    private Collection<ClickColumn<DocumentType>> columns = new LinkedList<>();

    private ClickColumnProvider<DocumentType> columnProvider = new ClickColumnProvider<>();

    @Inject
    EditClickColumn<DocumentType> editClickColumn;

    @Inject
    En_DocumentCategoryLang documentCategoryLang;

    AbstractDocumentTypeTableActivity activity;

    private static DocumentTypeTableViewUiBinder ourUiBinder = GWT.create(DocumentTypeTableViewUiBinder.class);

    interface DocumentTypeTableViewUiBinder extends UiBinder<HTMLPanel, DocumentTypeTableView> {
    }
}
