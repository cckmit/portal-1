package ru.protei.portal.ui.common.client.widget.selector.employeedepartment;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CompanyDepartmentControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public abstract class EmployeeDepartmentModel
        extends BaseSelectorModel<EntityOption> implements Activity {

    private List<EntityOption> options;

    @Override
    public EntityOption get(int elementIndex) {
        if(size( options ) <= elementIndex) return null;
        return options.get( elementIndex );
    }

    public void refreshOptions(Long personId) {
        if (personId == null) {
            options = Collections.emptyList();
            return;
        }
        departmentController.getPersonDepartments(personId, true, new FluentCallback<List<EntityOption>>()
                .withSuccess(result -> options = result));
    }

    @Inject
    CompanyDepartmentControllerAsync departmentController;
}
