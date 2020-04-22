package ru.protei.portal.ui.common.client.widget.selector.companydepartment;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;
import ru.protei.portal.ui.common.client.service.CompanyDepartmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.person.Refreshable;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Модель селектора отделов
 */
public abstract class CompanyDepartmentModel implements Activity, SelectorModel<EntityOption> {

    public void updateCompanyDepartments (Refreshable selector, Long companyId) {
        companyDepartmentController.getCompanyDepartments(companyId, new FluentCallback<List<CompanyDepartment>>()
                .withSuccess(companyDepartments -> {
                    fillEntityOptionList(companyDepartments);
                    if(selector!=null){
                        selector.refresh();
                    }
                })
        );
    }

    public Collection<EntityOption> getValues() {
        return options;
    }

    @Override
    public EntityOption get( int elementIndex ) {
        if(size( options ) <= elementIndex) return null;
        return options.get( elementIndex );
    }

    private void fillEntityOptionList (List<CompanyDepartment> companyDepartments) {
        options.clear();
        companyDepartments.forEach(companyDepartment -> options.add (new EntityOption(companyDepartment.getName(), companyDepartment.getId())));
    }


    @Inject
    CompanyDepartmentControllerAsync companyDepartmentController;

    private List<EntityOption> options = new ArrayList<>();
}