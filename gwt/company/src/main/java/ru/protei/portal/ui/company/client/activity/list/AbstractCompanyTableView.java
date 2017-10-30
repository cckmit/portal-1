package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

/**
 * Created by bondarenko on 30.10.17.
 */
public interface AbstractCompanyTableView extends IsWidget {

    void setActivity( CompanyTableActivity activity );
    void setAnimation ( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void setIssuesCount( Long issuesCount );

    int getPageSize();

    int getPageCount();

    void scrollTo( int page );

    void updateRow(Company item);

}
