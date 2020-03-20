package ru.protei.portal.ui.common.client.activity.filter;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;

import java.util.function.Function;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueFilterView extends IsWidget {
    void setModel(AbstractIssueFilterModel model);

    HasValue<String> filterName();

    HasVisibility removeFilterBtnVisibility();

    void resetFilter();

    HasEnabled createEnabled();

    void changeUserFilterValueName(CaseFilterShortView value);

    void addUserFilterDisplayOption(CaseFilterShortView value);

    void setFilterNameContainerErrorStyle(boolean hasError);

    void setUserFilterNameVisibility(boolean hasVisible);

    void setUserFilterControlsVisibility(boolean hasVisible);

    HasVisibility editBtnVisibility();

    AbstractIssueFilterWidgetView getIssueFilterParams();

    void presetFilterType();

    HasValue<CaseFilterShortView> userFilter();

    void updateFilterType(En_CaseFilterType filterType);

    void showUserFilterControls();

    void addAdditionalFilterValidate(Function<CaseFilter, Boolean> validate);

    CaseQuery getFilterFieldsByFilterType();
}