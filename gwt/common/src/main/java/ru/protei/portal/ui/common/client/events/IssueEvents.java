package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.internal.util.$AsynchronousComputationException;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;

/**
 * Created by turik on 28.10.16.
 */
public class IssueEvents {

    @Url( value = "issues", primary = true )
    public static class Show {

        public Show () {}

        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }

        public Show (CaseQuery query) {
            this.query = query;
        }

        public Show (CaseQuery query, Boolean preScroll) {
            this.query = query;
            this.preScroll = preScroll;
        }

        @Omit
        public CaseQuery query;
        @Omit
        public Boolean preScroll = false;
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
     * Показать форму редактирования
     */
    @Url( value = "issue_preview", primary = true )
    public static class ShowFullScreen {
        @Name( "id" )
        public Long issueCaseNumber;
    }

    /**
     * Показать форму редактирования
     */
    @Url(value = "issue")
    public static class Edit {
        @Name( "id" )
        public Long caseNumber;
        @Omit
        public Runnable backEvent;

        public Edit() { this.caseNumber = null; }
        public Edit (Long caseNumber) {
            this.caseNumber = caseNumber;
        }

        public Edit withBackEvent(Runnable backEvent) {
            this.backEvent = backEvent;
            return this;
        }
    }

    public static class EditMeta {
        public HasWidgets parent;

        public EditMeta(HasWidgets parent) {
            this.parent = parent;
        }

        public EditMeta withMeta(CaseObjectMeta meta) {
            this.meta = meta;
            return this;
        }

        public EditMeta withMetaNotifiers(CaseObjectMetaNotifiers metaNotifiers) {
            this.metaNotifiers = metaNotifiers;
            return this;
        }

        public EditMeta withMetaJira(CaseObjectMetaJira metaJira) {
            this.metaJira = metaJira;
            return this;
        }

        public EditMeta withReadOnly(boolean isReadOnly) {
            this.isReadOnly = isReadOnly;
            return this;
        }

        public CaseObjectMeta meta;
        public CaseObjectMetaNotifiers metaNotifiers;
        public CaseObjectMetaJira metaJira;
        public boolean isReadOnly = false;
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

    public static class IssueStateChanged {
        public IssueStateChanged( Long issueId, Long stateId ) {
            this.issueId = issueId;
            this.stateId = stateId;
        }
        public Long issueId;
        public Long stateId;
    }

    public static class IssueImportanceChanged {
        public IssueImportanceChanged( Long issueId ) {
            this.issueId = issueId;
        }
        public Long issueId;
    }

    public static class IssueManagerChanged {
        public IssueManagerChanged( Long issueId ) {
            this.issueId = issueId;
        }
        public Long issueId;
    }

    public static class IssueMetaChanged {
        public IssueMetaChanged(CaseObjectMeta meta) {
            this.meta = meta;
        }
        public CaseObjectMeta meta;
    }

    public static class IssueFavoriteStateChanged {
        public IssueFavoriteStateChanged(Long issueId, boolean isFavorite) {
            this.isFavorite = isFavorite;
            this.issueId = issueId;
        }

        public boolean isFavorite;
        public Long issueId;
    }
}

