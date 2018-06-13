package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

/**
 * Created by bondarenko on 30.10.17.
 */
public interface AbstractCompanyTableView extends IsWidget {

    void setActivity( AbstractCompanyTableActivity activity );
    void setAnimation ( TableAnimation animation );
    void clearRecords();
    HasWidgets getPreviewContainer ();
    HasWidgets getFilterContainer ();

    void setData(List<Company> companies);

}
