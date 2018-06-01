package ru.protei.portal.ui.documenttype.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractDocumentTypeTableView extends IsWidget {

    void setActivity(AbstractDocumentTypeTableActivity activity);

    void clearRecords();

    void addRow(DocumentType row);

    void updateRow(DocumentType project);

    void setAnimation(TableAnimation animation);

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();
}
