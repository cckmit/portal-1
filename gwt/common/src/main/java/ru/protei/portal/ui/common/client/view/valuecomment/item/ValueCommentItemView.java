package ru.protei.portal.ui.common.client.view.valuecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentItemActivity;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentItemView;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Представление элемента
 */
public class ValueCommentItemView extends Composite implements AbstractValueCommentItemView {
    public ValueCommentItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        comment.getElement().setPropertyString( "placeholder", lang.comment() );
    }

    @Override
    public void setActivity(AbstractValueCommentItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasText value() {
        return value;
    }

    @Override
    public HasText comment() {
        return comment;
    }

    @Override
    public void focused(){
        value.setFocus(true);
    }

    @UiHandler( "comment" )
    public void onChangeCommentField( KeyUpEvent event ) {
        activity.onChangeComment(this);
    }

    @UiHandler( "value" )
    public void onChangeInputField( KeyUpEvent event ) {
        activity.onChangeValue(this);
    }


    @UiField
    TextBox value;
    @UiField
    TextBox comment;

    @Inject
    @UiField
    Lang lang;

    AbstractValueCommentItemActivity activity;

    private static ValueCommentItemViewUiBinder ourUiBinder = GWT.create(ValueCommentItemViewUiBinder.class);
    interface ValueCommentItemViewUiBinder extends UiBinder<HTMLPanel, ValueCommentItemView> {}
}
