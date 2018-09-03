package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Questionnaire;

public class QuestionnaireEvents {

    @Url( value = "questionnaires", primary = true )
    public static class Show {
        public Show () {}
    }

    @Url( value = "questionnaire")
    public static class Create {
    }

    public static class ShowPreview {

        public ShowPreview (HasWidgets parent, Questionnaire questionnaire) {
            this.parent = parent;
            this.questionnaire = questionnaire;
        }

        public Questionnaire questionnaire;
        public HasWidgets parent;
    }
}
