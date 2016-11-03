package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция представления элемента списка
 */
public interface AbstractValueCommentItemView extends IsWidget {
    void setActivity(AbstractValueCommentItemActivity activity);

    HasText value();
    HasText comment();

    void focused();
}
