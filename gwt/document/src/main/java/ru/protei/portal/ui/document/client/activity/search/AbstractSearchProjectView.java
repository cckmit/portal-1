package ru.protei.portal.ui.document.client.activity.search;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.List;
import java.util.Set;

public interface AbstractSearchProjectView extends IsWidget {
    void setActivity(AbstractSearchProjectActivity activity);
    HasValue<String> name();
    HasValue<En_CustomerType> customerType();
    HasValue<Set<ProductShortView>> products();
    HasValue<DateInterval> dateCreatedRange();
    HasValue<ProjectInfo> project();
    void clearProjectList();
    void fillProjectList(List<ProjectInfo> list);
    HasWidgets createProjectContainer();
    void resetFilter();
}
