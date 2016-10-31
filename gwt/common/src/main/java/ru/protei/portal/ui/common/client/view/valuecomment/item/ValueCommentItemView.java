package ru.protei.portal.ui.common.client.view.valuecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
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

    @Override
    public void focused(){
        value.setFocus(true);
    }

    @Override
    public void setNew() {
        button.removeStyleName(ValueCommentStatus.FILLED.getButtonColor());
        button.addStyleName(ValueCommentStatus.NEW.getButtonColor());
        icon.setClassName(ValueCommentStatus.NEW.getButtonIcon());
        status = ValueCommentStatus.NEW;
    }

    @Override
    public void setFilled() {
        button.removeStyleName(ValueCommentStatus.NEW.getButtonColor());
        button.addStyleName(ValueCommentStatus.FILLED.getButtonColor());
        icon.setClassName(ValueCommentStatus.FILLED.getButtonIcon());
        status = ValueCommentStatus.FILLED;
    }

    @UiHandler( "button" )
    public void onMenuClicked( ClickEvent event ) {
        if(status == ValueCommentStatus.NEW)
            activity.onCreateClicked(this);
        else
            activity.onDeleteClicked(this);
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
