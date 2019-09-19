package ru.protei.portal.ui.common.client.view.notify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.notify.AbstractNotifyActivity;
import ru.protei.portal.ui.common.client.activity.notify.AbstractNotifyView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Представление уведомление
 */

public class NotifyView extends Composite implements AbstractNotifyView, ClickHandler {

    public NotifyView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        notify.sinkEvents(Event.ONCLICK);
        notify.addHandler(this, ClickEvent.getType());
        setTestAttributes();
        iconContainer.clear();
    }

    @Override
    public void setActivity( AbstractNotifyActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setMessage( String text ) {
        message.setInnerText(text);
    }

    @Override
    public void setType(NotifyEvents.NotifyType type) {
        iconContainer.clear();
        switch ( type ) {
            case INFO:
                return;
            case ERROR:
                iconContainer.add( iconError );
                return;
            case SUCCESS:
                iconContainer.add( iconSuccess );
        }
    }

    @Override
    public void onClick( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCloseClicked( this );
        }
    }

    private void setTestAttributes() {
        notify.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.NOTIFY.NOTIFY_ITEM);

        iconSuccess.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.NOTIFY.NOTIFY_ICON_SUCCESS);
        iconError.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.NOTIFY.NOTIFY_ICON_ERROR);

        message.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.NOTIFY.NOTIFY_CONTENT_MESSAGE);
        title.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.NOTIFY.NOTIFY_CONTENT_TITLE);
    }

    @UiField
    ParagraphElement message;
    @UiField
    HTMLPanel notify;
    @UiField
    SpanElement title;
    @UiField
    HTMLPanel iconSuccess;
    @UiField
    HTMLPanel iconError;
    @UiField
    HTMLPanel iconContainer;

    AbstractNotifyActivity activity;

    interface ErrorMessageViewUiBinder extends UiBinder<Widget, NotifyView > {}
    private static ErrorMessageViewUiBinder ourUiBinder = GWT.create( ErrorMessageViewUiBinder.class );
}