package ru.protei.portal.ui.issue.client.view.comment.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemActivity;
import ru.protei.portal.ui.issue.client.activity.comment.item.AbstractIssueCommentItemView;
import ru.protei.portal.ui.issue.client.activity.comment.label.AbstractIssueCommentLabelActivity;
import ru.protei.portal.ui.issue.client.activity.comment.label.AbstractIssueCommentLabelView;

/**
 * Один комментарий
 */
public class IssueCommentLabelView
        extends Composite
        implements AbstractIssueCommentLabelView {


    public IssueCommentLabelView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractIssueCommentLabelActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setDate( String value ) {
        this.date.setInnerText( value );
    }

    @Override
    public void setOwner( String value ) {
        this.owner.setInnerText( value );
    }

    @Override
    public void setStatus( En_CaseState value ) {
        status.setClassName( "case-" + value.name().toLowerCase() );
        this.status.setInnerText( stateLang.getStateName( value ) );
    }

    @UiField
    SpanElement owner;
    @UiField
    DivElement date;
    @UiField
    HTMLPanel root;
    @UiField
    SpanElement status;

    @Inject
    En_CaseStateLang stateLang;

    private AbstractIssueCommentLabelActivity activity;

    interface IssueCommentUiBinder extends UiBinder<Widget, IssueCommentLabelView> {}
    private static IssueCommentUiBinder ourUiBinder = GWT.create( IssueCommentUiBinder.class );
}