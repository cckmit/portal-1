package ru.protei.portal.ui.questionnaire.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.questionnaire.client.activity.page.QuestionnairePage;
import ru.protei.portal.ui.questionnaire.client.activity.table.AbstractQuestionnaireTableView;
import ru.protei.portal.ui.questionnaire.client.activity.table.QuestionnaireTableActivity;
import ru.protei.portal.ui.questionnaire.client.view.table.QuestionnaireTableView;

public class QuestionnaireClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind(QuestionnairePage.class).asEagerSingleton();

        bind(QuestionnaireTableActivity.class).asEagerSingleton();
        bind(AbstractQuestionnaireTableView.class).to(QuestionnaireTableView.class).in(Singleton.class);
    }
}

