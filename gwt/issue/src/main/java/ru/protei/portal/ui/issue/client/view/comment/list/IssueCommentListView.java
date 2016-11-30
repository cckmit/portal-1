package ru.protei.portal.ui.issue.client.view.comment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.item.AutoAddVCItem;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListActivity;
import ru.protei.portal.ui.issue.client.activity.comment.list.AbstractIssueCommentListView;
import ru.protei.portal.ui.issue.client.view.comment.item.IssueCommentItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Контейнер для комментариев
 */
public class IssueCommentListView
        extends Composite
        implements AbstractIssueCommentListView {


    public IssueCommentListView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractIssueCommentListActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @UiField
    HTMLPanel root;
    @UiField
    TextBox addComment;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    Button apply;

    private AbstractIssueCommentListActivity activity;

    private static IssueListUiBinder ourUiBinder = GWT.create(IssueListUiBinder.class);
    interface IssueListUiBinder extends UiBinder<HTMLPanel, IssueCommentListView> {}
}