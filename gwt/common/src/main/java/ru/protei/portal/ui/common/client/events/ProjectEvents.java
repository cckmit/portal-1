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
     * Показать превью проекта
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, Long projectId )
        {
            this.parent = parent;
            this.projectId = projectId;
        }

        public HasWidgets parent;
        public Long projectId;

    }

    /**
     * Показать превью проекта full screen
     */
    @Url( value = "project_preview", primary = true )
    public static class ShowFullScreen {

        public ShowFullScreen() {}

        public ShowFullScreen ( Long id )
        {
            this.projectId = id;
        }

        @Name( "id" )
        public Long projectId;
    }

    @Url( value = "project", primary = false )
    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit ( Long id ) {
            this.id = id;
        }
    }

    /**
     * Изменения статусов проекта
     */
    public static class ChangeStateModel {}

    /**
     * Изменение модели проектов
     */
    public static class ChangeModel {}

    /**
     * Изменение проекта
     */
    public static class ChangeProject {
        public Long id;

        public ChangeProject(Long projectId){
            id = projectId;
        }
    }

    public static class ShowProjectDocuments {

        public ShowProjectDocuments(HasWidgets parent, Long projectId) {
            this.parent = parent;
            this.projectId = projectId;
            this.isModifyEnabled = true;
        }

        public ShowProjectDocuments(HasWidgets parent, Long projectId, boolean isModifyEnabled) {
            this.parent = parent;
            this.projectId = projectId;
            this.isModifyEnabled = isModifyEnabled;
        }

        public Long projectId;
        public HasWidgets parent;
        public boolean isModifyEnabled;
    }
}

