package ru.protei.portal.ui.common.client.widget.attachment.list;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.Collection;

/**
 * Created by bondarenko on 17.01.17.
 */
public interface HasAttachments extends HasWidgets {

    void add(Attachment attachment);

    void remove(Attachment attachment);

    void clear();

    Collection<Attachment> getAll();

}
