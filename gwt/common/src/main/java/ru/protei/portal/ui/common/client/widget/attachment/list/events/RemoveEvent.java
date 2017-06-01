package ru.protei.portal.ui.common.client.widget.attachment.list.events;

import com.google.gwt.event.shared.GwtEvent;
import ru.protei.portal.core.model.ent.Attachment;

/**
 * Created by bondarenko on 17.01.17.
 */
public class RemoveEvent extends GwtEvent<RemoveHandler> {
    private static Type<RemoveHandler> TYPE = new Type<>();
    private Attachment attachment;

    public static void fire( HasAttachmentListHandlers source, Attachment attachment ) {
        if(TYPE != null) {
            source.fireEvent( new RemoveEvent(attachment) );
        }
    }

    public static Type<RemoveHandler> getType() {
        return TYPE;
    }

    protected RemoveEvent(Attachment attachment){
        this.attachment = attachment;
    }

    protected void dispatch(RemoveHandler handler) {
        handler.onRemove( this );
    }

    public final Type<RemoveHandler> getAssociatedType() {
        return TYPE;
    }

    public String toDebugString() {
        return super.toDebugString() ;
    }

    public Attachment getAttachment() {
        return attachment;
    }
}
