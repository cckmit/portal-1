package ru.protei.portal.ui.issue.client.activity.create.subtask;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.company.SubcontractorCompanyModel;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractSubtaskCreateView extends IsWidget {
    void setActivity(AbstractSubtaskCreateActivity activity);
    HasValue<String> name();
    HasValue<String> description();
    HasValidable nameValidator();
    String DESCRIPTION = "description";
    HasValue<EntityOption> managerCompany();
    HasValidable managerCompanyValidator();
    HasValue<PersonShortView> manager();
    void setManagerCompanyModel(SubcontractorCompanyModel subcontractorCompanyModel);
    void updateManagersCompanyFilter(Long managerCompanyId);
}