package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentDataList;

/**
 * Абстракция представления списка
 */
public interface AbstractValueCommentListView extends IsWidget{

    HasWidgets getItemsContainer();

}
