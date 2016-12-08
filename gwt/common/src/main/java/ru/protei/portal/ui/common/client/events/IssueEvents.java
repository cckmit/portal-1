package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
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
     * Показать обращения с выбранными фильтрами и контейнером
     */
    public static class ShowCustom {

        public CaseQuery query;
        public HasWidgets parent;
        public Runnable afterRequestAction;
        public ShowCustom (CaseQuery query, HasWidgets parent) {
            if(query == null || parent == null || query.getType() != En_CaseType.CRM_SUPPORT)
                throw new IllegalArgumentException("query type must be for CRM_SUPPORT");

            this.query = query;
            this.parent = parent;
        }

        /**
         * @param afterRequestAction Операция, которая будет выполнена после всех работ
         */
        public ShowCustom (CaseQuery query, HasWidgets parent, Runnable afterRequestAction) {
            this(query, parent);
            this.afterRequestAction = afterRequestAction;
        }

    }

    /**
     * Показать превью обращения
     */
    public static class ShowPreview {

        public ShowPreview ( HasWidgets parent, CaseObject issue )
        {
            this.parent = parent;
            this.issue = issue;
        }

        public CaseObject issue;
        public HasWidgets parent;

    }

    /**
     * Показать превью обращения full screen
     */
    @Url( value = "issue_preview", primary = true )
    public static class ShowFullScreen {

        public ShowFullScreen() {}

        public ShowFullScreen ( Long id )
        {
            this.issueId = id;
        }

        @Name( "id" )
        public Long issueId;
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
}

