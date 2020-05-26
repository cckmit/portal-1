package ru.protei.portal.ui.employeeregistration.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

import java.util.Date;
import java.util.Set;

public interface AbstractEmployeeRegistrationEditView extends IsWidget {
    void setActivity(AbstractEmployeeRegistrationEditActivity activity);

    HasValue<Date> employmentDate();

    HasValue<Set<PersonShortView>> curators();

    void setCuratorsFilter(Selector.SelectorFilter<PersonShortView> curatorsFilter);
}
