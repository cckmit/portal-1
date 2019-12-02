package ru.protei.portal.ui.document.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.ui.common.client.columns.*;

public interface AbstractDocumentTableActivity
        extends ClickColumn.Handler<Document>, EditClickColumn.EditHandler<Document>,
        InfiniteLoadHandler<Document>, InfiniteTableWidget.PagerListener,
        DownloadClickColumn.DownloadHandler<Document>, ArchiveClickColumn.ArchiveHandler<Document>,
        RemoveClickColumn.RemoveHandler<Document> {

    void onProjectColumnClicked(Document value);
}
