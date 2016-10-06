package ru.protei.portal.ui.common.client.notify.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.notify.activity.AbstractNotifyActivity;
import ru.protei.portal.ui.common.client.notify.activity.AbstractNotifyView;

/**
 * Представление уведомление
 */
public class NotifyView extends Composite implements AbstractNotifyView {

    public NotifyView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractNotifyActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setMessage( String text ) {
        message.setInnerHTML( text ); // ??? может быть не просто текст, а еще и верстка внутри?
    }

    @Override
    public void setType(String type) {
        notify.addStyleName(type);
    }

    @Override
    public void setTitle( String title ) {
        header.setInnerText( title );
    }

    @UiHandler( "close" )
    public void onCloseClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCloseClicked( this );
        }
    }

    @UiField
    Button close;
    @UiField
    SpanElement header;
    @UiField
    ParagraphElement message;
    @UiField
    HTMLPanel notify;

    AbstractNotifyActivity activity;

    interface ErrorMessageViewUiBinder extends UiBinder<Widget, NotifyView > {}
    private static ErrorMessageViewUiBinder ourUiBinder = GWT.create( ErrorMessageViewUiBinder.class );
}
