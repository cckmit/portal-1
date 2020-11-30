package ru.protei.portal.ui.common.client.activity.companydepartment.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.CompanyDepartmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.CompanyDepartmentControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class CompanyDepartmentEditActivity implements Activity, AbstractCompanyDepartmentEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(CompanyDepartmentEvents.Edit event) {
        if ( event.companyDepartment == null ) {
            fireEvent(new NotifyEvents.Show(lang.error(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.companyDepartment = event.companyDepartment;
        view.nameEnabled().setEnabled(true);
        view.name().setValue(companyDepartment.getName());
        setCompanyEntityOption(companyDepartment.getCompanyId());

        view.companyEnabled().setEnabled(true);

        dialogView.removeButtonVisibility().setVisible(true);
        dialogView.saveButtonVisibility().setVisible(true);
        dialogView.setHeader(companyDepartment.getId() == null ? lang.departmentCreate() : lang.departmentEdit());
        dialogView.showPopup();
    }

    private void setCompanyEntityOption (Long companyId){
        companyService.getCompanyOptionListIgnorePrivileges(new CompanyQuery(true).onlyVisibleFields(),
                new FluentCallback<List<EntityOption>>()
                        .withSuccess(companies -> {
                            view.company().setValue(companies.stream().filter(company -> company.getId().equals(companyId)).findFirst().get());
                        }));
    }

    @Override
    public void onRemoveClicked() {
        companyDepartmentController.removeCompanyDepartment(companyDepartment, new FluentCallback<Long>()
                .withSuccess(result -> {
                    dialogView.hidePopup();
                    fireEvent(new CompanyDepartmentEvents.Removed(companyDepartment));
                })
        );
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            return;
        }

        companyDepartment.setName(view.name().getValue());
        companyDepartment.setCompanyId(view.company().getValue().getId());

        if (isNew(companyDepartment)) {
            companyDepartmentController.createCompanyDepartment(companyDepartment, new FluentCallback<Long>()
                    .withSuccess( id -> {
                        companyDepartment.setId( id );
                        dialogView.hidePopup();
                        fireEvent( new CompanyDepartmentEvents.Created(companyDepartment) );
                    } )
            );
            return;
        }

        companyDepartmentController.updateCompanyDepartmentName(companyDepartment, new FluentCallback<Long>()
                .withSuccess(id -> {
                    dialogView.hidePopup();
                    fireEvent(new CompanyDepartmentEvents.Changed(companyDepartment));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private boolean validate() {
        return !StringUtils.isBlank(view.name().getValue());
    }

    private boolean isNew( CompanyDepartment companyDepartment ) {
        return companyDepartment!=null && companyDepartment.getId() == null;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractCompanyDepartmentEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    CompanyDepartmentControllerAsync companyDepartmentController;
    @Inject
    private CompanyControllerAsync companyService;

    private CompanyDepartment companyDepartment;
}
