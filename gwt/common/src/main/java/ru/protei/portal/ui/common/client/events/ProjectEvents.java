package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;

/**
 * События для вкладки с проектами
 */
public class ProjectEvents {

    @Url( value = "projects", primary = true )
    public static class Show {
        public Show () {}
    }

    /**
     * Показать превью обращения
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, Long issueId )
        {
            this.parent = parent;
            this.issueId = issueId;
        }

        public HasWidgets parent;
        public Long issueId;

    }

    /**
     * Показать превью проекта full screen
     */
    @Url( value = "project_preview", primary = true )
    public static class ShowFullScreen {

        public ShowFullScreen() {}

        public ShowFullScreen ( Long id )
        {
            this.issueId = id;
        }

        @Name( "id" )
        public Long issueId;
    }

    @Url( value = "project", primary = false )
    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit ( Long id ) {
            this.id = id;
        }

        public static Edit byId (Long id) {
            return new Edit(id);
        }
    }

    /**
     * Изменения статусов проекта
     */
    public static class ChangeStateModel {}

    /**
     * Добавление / изменение / удаление проектов
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
}

