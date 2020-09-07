package ru.protei.portal.ui.issueassignment.client.activity.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;
import java.util.function.Predicate;

public interface AbstractTableView extends IsWidget {

    void setActivity(AbstractTableActivity activity);

    void clearRecords();

    void putRecords(List<CaseShortView> list);

    void setTotalRecords(int totalRecords);

    void showLoader(boolean isShow);

    void showTableOverflow(int showedRecords);

    void hideTableOverflow();

    void updateFilterSelector();

    HasValue<CaseFilterShortView> filter();
}
