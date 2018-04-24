package ru.protei.portal.ui.documenttype.client.activity.table;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DocumentType;

public interface AbstractDocumentTypeTableView extends IsWidget {

    void setActivity(AbstractDocumentTypeTableActivity activity);

    void clearRecords();

    void addRow(DocumentType row);

    void updateRow(DocumentType project);
}
