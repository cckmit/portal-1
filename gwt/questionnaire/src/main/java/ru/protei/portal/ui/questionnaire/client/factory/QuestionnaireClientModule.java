package ru.protei.portal.ui.questionnaire.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.questionnaire.client.activity.edit.AbstractQuestionnaireEditView;
import ru.protei.portal.ui.questionnaire.client.activity.edit.QuestionnaireEditActivity;
import ru.protei.portal.ui.questionnaire.client.activity.filter.AbstractQuestionnaireFilterView;
import ru.protei.portal.ui.questionnaire.client.activity.page.QuestionnairePage;
import ru.protei.portal.ui.questionnaire.client.activity.preview.AbstractQuestionnairePreviewView;
import ru.protei.portal.ui.questionnaire.client.activity.preview.QuestionnairePreviewActivity;
import ru.protei.portal.ui.questionnaire.client.activity.table.AbstractQuestionnaireTableView;
import ru.protei.portal.ui.questionnaire.client.activity.table.QuestionnaireTableActivity;
import ru.protei.portal.ui.questionnaire.client.view.edit.QuestionnaireEditView;
import ru.protei.portal.ui.questionnaire.client.view.filter.QuestionnaireFilterView;
import ru.protei.portal.ui.questionnaire.client.view.preview.QuestionnairePreviewView;
import ru.protei.portal.ui.questionnaire.client.view.table.QuestionnaireTableView;

public class QuestionnaireClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind(QuestionnairePage.class).asEagerSingleton();

        bind(QuestionnaireTableActivity.class).asEagerSingleton();
        bind(AbstractQuestionnaireTableView.class).to(QuestionnaireTableView.class).in(Singleton.class);

        bind(QuestionnaireEditActivity.class).asEagerSingleton();
        bind(AbstractQuestionnaireEditView.class).to(QuestionnaireEditView.class).in(Singleton.class);

        bind(QuestionnairePreviewActivity.class).asEagerSingleton();
        bind(AbstractQuestionnairePreviewView.class).to(QuestionnairePreviewView.class).in(Singleton.class);

        bind(AbstractQuestionnaireFilterView.class).to(QuestionnaireFilterView.class).in(Singleton.class);
    }
}

