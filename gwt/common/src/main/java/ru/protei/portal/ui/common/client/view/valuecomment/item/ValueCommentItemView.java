package ru.protei.portal.ui.common.client.view.valuecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentItemActivity;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentItemView;
import ru.protei.portal.ui.common.client.activity.valuecomment.ValueCommentStatus;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;

/**
 * Created by bondarenko on 28.10.16.
 */
public class ValueCommentItemView extends Composite implements AbstractValueCommentItemView {
    public ValueCommentItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
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

    @UiHandler( "button" )
    public void onMenuClicked( ClickEvent event ) {
        if(status == ValueCommentStatus.NEW)
            activity.onCreateClicked();
        else
            activity.onDeleteClicked(this);
    }

    public void focused(){
        value.setFocus(true);
    }

    public void updateStatus(ValueCommentStatus status){
        this.status = status;
        ValueCommentStatus anotherStatus;
        if(status == ValueCommentStatus.NEW)
            anotherStatus = ValueCommentStatus.FILLED;
        else
            anotherStatus = ValueCommentStatus.NEW;

        button.getElement().removeClassName(anotherStatus.getButtonColor());
        button.getElement().addClassName(status.getButtonColor());
        icon.setClassName(status.getButtonIcon());
    }

    @UiField
    TextBox value;
    @UiField
    TextBox comment;
    @UiField
    Button button;
    @UiField
    Element icon;

    AbstractValueCommentItemActivity activity;
    ValueCommentStatus status;

    private static ValueCommentItemViewUiBinder ourUiBinder = GWT.create(ValueCommentItemViewUiBinder.class);
    interface ValueCommentItemViewUiBinder extends UiBinder<HTMLPanel, ValueCommentItemView> {}
}
