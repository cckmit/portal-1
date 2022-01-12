package ru.protei.portal.ui.common.client.activity.ytwork.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;

import java.util.List;

public interface AbstractYoutrackWorkDictionaryTableView extends IsWidget {
    void setActivity(AbstractYoutrackWorkDictionaryTableActivity activity);

    void clearRecords();

    void putRecords(List<YoutrackWorkDictionary> list);

    void setName(String name);

    void setCollapsed(boolean isCollapsed);

    void setRecords(int filteredRecords, int totalRecords);

    void showLoader(boolean isShow);

    void showTableOverflow(int showedRecords);

    void hideTableOverflow();

    void setEnsureDebugId(String debugId);

    void refreshTable();

    void presetScroll();

    void resetScroll();

    HasValue<String> searchPattern();
}
