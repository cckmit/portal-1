package ru.protei.portal.ui.common.client.widget.attachment.list;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.Collection;

/**
 * Created by bondarenko on 17.01.17.
 */
public interface HasAttachments extends IsWidget {

    void add(Attachment attachment);

    void add(Collection<Attachment> attachments);

    void remove(Attachment attachment);

    void clear();

    boolean isEmpty();

    Collection<Attachment> getAll();

}
