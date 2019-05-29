package ru.protei.portal.ui.company.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Created by bondarenko on 30.10.17.
 */
public interface AbstractCompanyTableView extends IsWidget {

    void setActivity( AbstractCompanyTableActivity activity );
    void setAnimation ( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void setCompaniesCount(Long issuesCount );

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo( int page );

    void updateRow(Company item);

    HasWidgets getPagerContainer();
}
