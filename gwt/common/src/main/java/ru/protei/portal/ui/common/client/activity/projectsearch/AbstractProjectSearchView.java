package ru.protei.portal.ui.common.client.activity.projectsearch;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.List;
import java.util.Set;

/**
 * Представление поиска проекта
 */
public interface AbstractProjectSearchView extends IsWidget {
    void setActivity( AbstractProjectSearchActivity activity);
    HasValue<String> name();
    HasValue<En_CustomerType> customerType();

    HasValue<Long> id();

    HasValue<Set<ProductShortView>> products();
    HasValue<Set<PersonShortView>> managers();
    HasValue<DateInterval> dateCreatedRange();
    HasValue<ProjectInfo> project();

    void setVisibleProducts(boolean value);
    void setVisibleManagers(boolean value);

    void clearProjectList();
    void fillProjectList(List<ProjectInfo> list);
    void resetFilter();

    void setSeparateFormView(boolean isSeparateFormView);
}
