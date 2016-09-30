package ru.protei.portal.ui.company.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;

/**
 * Created by turik on 30.09.16.
 */
public interface AbstractCompanyItemView extends IsWidget {
    void setActivity(AbstractCompanyListActivity activity);
    void setName(String name);
    void setType(String type);
}
