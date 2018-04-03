package ru.protei.portal.ui.common.client.activity.pager;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by shagaleev on 11/30/16.
 */
public interface AbstractPagerView extends IsWidget {
    void setActivity( AbstractPagerActivity activity );

    void setCurrentPage( int value );

    void setTotalCount( long value);

    void setTotalPages(int value);
}
