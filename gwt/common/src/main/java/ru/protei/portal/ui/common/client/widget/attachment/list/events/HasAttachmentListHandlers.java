package ru.protei.portal.ui.common.client.widget.attachment.list.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Created by bondarenko on 17.01.17.
 */
public interface HasAttachmentListHandlers extends HasHandlers {

    HandlerRegistration addRemoveHandler(RemoveHandler handler );

}
