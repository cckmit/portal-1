package ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.handler.ItemChangeHandler;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentPair;

/**
 * Одна пара значение-комментарий с кнопкой
 */
public class AutoAddVCItem extends Composite implements ValueCommentPair {

    @Inject
    public AutoAddVCItem(ItemChangeHandler handler) {
        initWidget(ourUiBinder.createAndBindUi(this));

        this.handler = handler;
        updateStatus(AutoAddVCItemStatus.NEW);

        comment.getElement().setPropertyString( "placeholder", lang.comment() );
    }

    public AutoAddVCItem(ItemChangeHandler handler, String value, String comment) {
        this(handler);

        value().setText(value);
        comment().setText(comment);
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
        if(status == AutoAddVCItemStatus.NEW)
            handler.onAdd();
        else
            handler.onRemove(this);
    }

    public void focused(){
        value.setFocus(true);
    }

    public void updateStatus(AutoAddVCItemStatus status){
        this.status = status;
        AutoAddVCItemStatus anotherStatus;
        if(status == AutoAddVCItemStatus.NEW)
            anotherStatus = AutoAddVCItemStatus.FILLED;
        else
            anotherStatus = AutoAddVCItemStatus.NEW;

        button.getElement().removeClassName(anotherStatus.getButtonColor());
        button.getElement().addClassName(status.getButtonColor());
        icon.setClassName(status.getButtonIcon());
    }


    @UiField
    Button button;
    @UiField
    TextBox comment;
    @UiField
    TextBox value;
    @UiField
    Element icon;

    @Inject
    @UiField
    Lang lang;

    ItemChangeHandler handler;
    AutoAddVCItemStatus status;

    private static InputItemUiBinder ourUiBinder = GWT.create(InputItemUiBinder.class);
    interface InputItemUiBinder extends UiBinder<HTMLPanel, AutoAddVCItem> {}
}