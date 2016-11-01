package ru.protei.portal.ui.contact.client.activity.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

/**
 * Created by turik on 28.10.16.
 */
public interface AbstractContactTableView extends IsWidget {

    void setActivity( AbstractContactTableActivity activity );
    HasValue< Company > company();
    HasValue< Boolean > showFired();
    HasValue< En_SortField > sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();
    void clearRecords();
    void addRecords( List< Person > result );
}
