package ru.protei.portal.ui.project.client.activity.search;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.ProjectInfo;
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
    HasValue<Set<ProductShortView>> products();
    HasValue<DateInterval> dateCreatedRange();
    HasValue<ProjectInfo> project();
    void clearProjectList();
    void fillProjectList(List<ProjectInfo> list);
    void resetFilter();
    void refreshProducts();
}
