package ru.protei.portal.ui.common.client.notify.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.notify.activity.AbstractNotifyActivity;
import ru.protei.portal.ui.common.client.notify.activity.AbstractNotifyView;

/**
 * Представление уведомление
 */
public class NotifyView extends Composite implements AbstractNotifyView, ClickHandler {

    public NotifyView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        notify.sinkEvents(Event.ONCLICK);
        notify.addHandler(this, ClickEvent.getType());
    }

    @Override
    public void setActivity( AbstractNotifyActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setMessage( String text ) {
        message.setInnerText(text); // ??? может быть не просто текст, а еще и верстка внутри?
    }

    @Override
    public void setType(String type) {
        notify.addStyleName(type);
    }

//    @Override
//    public void setTitle( String title ) {
//        header.setInnerText( title );
//    }

    @Override
    public void onClick( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCloseClicked( this );
        }
    }

    @UiField
    SpanElement message;
    @UiField
    HTMLPanel notify;

    AbstractNotifyActivity activity;

    interface ErrorMessageViewUiBinder extends UiBinder<Widget, NotifyView > {}
    private static ErrorMessageViewUiBinder ourUiBinder = GWT.create( ErrorMessageViewUiBinder.class );
}
