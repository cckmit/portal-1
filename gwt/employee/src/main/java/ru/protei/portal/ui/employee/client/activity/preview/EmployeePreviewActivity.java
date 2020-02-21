package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;

/**
 * Активность превью сотрудника
 */
public abstract class EmployeePreviewActivity implements AbstractEmployeePreviewActivity, AbstractPositionItemActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( EmployeeEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.employee );
    }

    private void fillView(EmployeeShortView employee) {
        view.setID(employee.getId().toString());
        view.setName(employee.getDisplayName());
        view.setIP(employee.getIpAddress());

        view.getPositionsContainer().clear();
        WorkerEntryFacade entryFacade = new WorkerEntryFacade(employee.getWorkerEntries());
        entryFacade.getSortedEntries().forEach(workerEntry -> employeeService.getDepartmentHead(workerEntry.getDepId(), new FluentCallback<PersonShortView>()
                .withSuccess(head -> {
                    AbstractPositionItemView itemView = makeView(workerEntry, head == null || head.getId().equals(workerEntry.getPersonId()) ? null : head.getName());
                    view.getPositionsContainer().add(itemView.asWidget());
                })
        ));
    }

    private AbstractPositionItemView makeView(WorkerEntryShortView workerEntry, String headName) {
        AbstractPositionItemView itemView = factory.get();
        itemView.setActivity(this);

        if (workerEntry.getDepartmentParentName() == null) {
            itemView.setDepartmentParent(workerEntry.getDepartmentName());
        } else {
            itemView.setDepartmentParent(workerEntry.getDepartmentParentName());
            itemView.setDepartment(workerEntry.getDepartmentName());
            itemView.departmentContainerVisibility().setVisible(true);
        }

        if (headName != null) {
            itemView.setDepartmentHead(headName);
            itemView.departmentHeadContainerVisibility().setVisible(true);
        }

        itemView.setPosition(workerEntry.getPositionName());

        return itemView;
    }

    @Inject
    AbstractEmployeePreviewView view;

    @Inject
    Provider< AbstractPositionItemView > factory;

    @Inject
    EmployeeControllerAsync employeeService;
}
