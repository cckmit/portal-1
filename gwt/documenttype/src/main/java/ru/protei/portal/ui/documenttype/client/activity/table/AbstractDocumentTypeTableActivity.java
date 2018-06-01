package ru.protei.portal.ui.documenttype.client.activity.table;

import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractDocumentTypeTableActivity
        extends ClickColumn.Handler<DocumentType>, EditClickColumn.EditHandler<DocumentType>,
        RemoveClickColumn.RemoveHandler<DocumentType> {
}
