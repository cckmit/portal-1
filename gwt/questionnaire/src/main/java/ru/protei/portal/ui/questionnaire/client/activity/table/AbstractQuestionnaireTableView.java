package ru.protei.portal.ui.questionnaire.client.activity.table;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractQuestionnaireTableView extends IsWidget {
    void setActivity(AbstractQuestionnaireTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void setRecordCount(int count);

    HasWidgets getPreviewContainer();

    HTMLPanel getFilterContainer();
}
