package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by turik on 28.10.16.
 */
public class IssueEvents {

    @Url( value = "issues", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать превью обращения
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, Long issueCaseNumber )
        {
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

    @Url( value = "issue", primary = false )
    public static class Edit {

        public Long id;
        public Long issueId;

        public Edit() { this.id = null; }
        public Edit (Long id, Long issueId ) {
            this.id = id;
            this.issueId = issueId;
        }

        public static Edit byId (Long id) {
            return new Edit(id, null);
        }

        public static Edit newItem (EntityOption option) {
            return new Edit(null, option != null ? option.getId() : null);
        }
    }

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
     * Добавление / изменение / удаление обращений
     */
    public static class ChangeModel {}

    /**
     * Показать комментарии
     */
    public static class ShowComments {
        public ShowComments( HasWidgets parent, Long caseId ) {
            this.parent = parent;
            this.caseId = caseId;
        }

        public Long caseId;
        public HasWidgets parent;

    }

    public static class SaveComment {
        public SaveComment( Long id ) {
            this.id = id;
        }
        public Long id;
    }

    /**
     * Обновилось отображение комментариев
     */
    public static class ChangeCommentsView{}

    /**
     * Изменилась модель фильтров пользователя
     */
    public static class ChangeUserFilterModel{}
}

