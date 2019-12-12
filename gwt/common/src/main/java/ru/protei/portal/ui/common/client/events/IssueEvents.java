package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by turik on 28.10.16.
 */
public class IssueEvents {

    @Url( value = "issues", primary = true )
    public static class Show {

        public Show () {}

        public Show (Boolean clearScroll) {
            this.clearScroll = clearScroll;
        }

        public Show (CaseQuery query) {
            this.query = query;
        }

        public Show (CaseQuery query, Boolean clearScroll) {
            this.query = query;
            this.clearScroll = clearScroll;
        }

        @Omit
        public CaseQuery query;
        @Omit
        public Boolean clearScroll = false;
    }

    /**
     * Показать превью обращения
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, Long issueCaseNumber ) {
            this.parent = parent;
            this.issueCaseNumber = issueCaseNumber;
        }

        public HasWidgets parent;
        public Long issueCaseNumber;

    }

    /**
     * Показать превью обращения full screen
     */
    @Url( value = "issue_preview", primary = true )
    public static class ShowFullScreen {

        public ShowFullScreen() {}

        public ShowFullScreen ( Long issueCaseNumber )
        {
            this.issueCaseNumber = issueCaseNumber;
        }

        @Name( "id" )
        public Long issueCaseNumber;
    }

    /**
     * Показать форму редактирования
     */
    @Url(value = "issue")
    public static class Edit {
        public Long id;

        public Edit() { this.id = null; }
        public Edit (Long id) {
            this.id = id;
        }
    }

    public static class EditMeta {
        public HasWidgets parent;

        public EditMeta( HasWidgets parent, CaseObjectMeta meta, CaseObjectMetaNotifiers metaNotifiers, CaseObjectMetaJira metaJira ) {
            this.parent = parent;
            this.meta = meta;
            this.metaNotifiers = metaNotifiers;
            this.metaJira = metaJira;
        }

        public CaseObjectMeta meta;
        public CaseObjectMetaNotifiers metaNotifiers;
        public CaseObjectMetaJira metaJira;
    }


    /**
     * Показать форму создания
     */
    @Url(value = "issue_create")
    public static class Create {}

    public static class ChangeIssue {
        public Long id;
        public ChangeIssue(Long issueId){
            id = issueId;
        }
    }

    /**
     * Изменения статусов обращения
     */
    public static class ChangeStateModel {}

   /**
     * Изменилась модель фильтров пользователя
     */
    public static class ChangeUserFilterModel{}

    public static class ChangeTimeElapsed {
        public ChangeTimeElapsed(Long timeElapsed) {
            this.timeElapsed = timeElapsed;
        }
        public Long timeElapsed;
    }
}

