package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;

/**
 * Created by bondarenko on 28.10.16.
 */
public interface AbstractValueCommentItemView extends ValueComment, IsWidget {
    void setActivity(AbstractValueCommentItemActivity activity);
}
