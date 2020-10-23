package ru.protei.portal.ui.common.client.activity.workerposition.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.WorkerPosition;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.WorkerPositionEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.WorkerPositionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class WorkerPositionEditActivity implements Activity, AbstractWorkerPositionEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(WorkerPositionEvents.Edit event) {
        if ( event.workerPosition == null ) {
            fireEvent(new NotifyEvents.Show(lang.error(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.workerPosition = event.workerPosition;
        view.nameEnabled().setEnabled(true);
        view.name().setValue(workerPosition.getName());
        setCompanyEntityOption(workerPosition.getCompanyId());

        view.companyEnabled().setEnabled(true);

        dialogView.removeButtonVisibility().setVisible(true);
        dialogView.saveButtonVisibility().setVisible(true);
        dialogView.setHeader(workerPosition.getId() == null ? lang.positionCreate() : lang.positionEdit());
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
        workerPositionController.removeWorkerPosition(workerPosition, new FluentCallback<Boolean>()
                .withSuccess(v -> {
                    dialogView.hidePopup();
                    fireEvent(new WorkerPositionEvents.Removed(workerPosition));
                })
        );
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            return;
        }

        workerPosition.setName(view.name().getValue());
        workerPosition.setCompanyId(view.company().getValue().getId());

        if (isNew(workerPosition)) {
            workerPositionController.createWorkerPosition(workerPosition, new FluentCallback<Long>()
                    .withSuccess( id -> {
                        workerPosition.setId( id );
                        dialogView.hidePopup();
                        fireEvent( new WorkerPositionEvents.Created(workerPosition) );
                    } )
            );
            return;
        }

        workerPositionController.updateWorkerPosition(workerPosition, new FluentCallback<Long>()
                .withSuccess(id -> {
                    dialogView.hidePopup();
                    fireEvent(new WorkerPositionEvents.Changed(workerPosition));
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

    private boolean isNew( WorkerPosition workerPosition ) {
        return workerPosition!=null && workerPosition.getId() == null;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractWorkerPositionEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    WorkerPositionControllerAsync workerPositionController;
    @Inject
    private CompanyControllerAsync companyService;

    private WorkerPosition workerPosition;
}
