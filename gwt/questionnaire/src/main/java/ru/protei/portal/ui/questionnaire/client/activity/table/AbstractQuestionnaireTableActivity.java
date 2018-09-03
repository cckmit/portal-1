package ru.protei.portal.ui.questionnaire.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.ui.common.client.columns.ClickColumn;

public interface AbstractQuestionnaireTableActivity extends ClickColumn.Handler<Questionnaire>,
        InfiniteLoadHandler<Questionnaire> {
}
