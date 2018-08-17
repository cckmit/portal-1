package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
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

    private void fillView( Person employee ) {

        view.setID( employee.getId().toString() );
        view.setIP( employee.getIpAddress() );

        view.getPositionsContainer().clear();
        WorkerEntryFacade entryFacade = new WorkerEntryFacade( employee.getWorkerEntries() );
        entryFacade.getSortedEntries().forEach( workerEntry -> {
            AbstractPositionItemView itemView = makeView( workerEntry );
            view.getPositionsContainer().add( itemView.asWidget() );
        } );
    }

    private AbstractPositionItemView makeView( WorkerEntry workerEntry ) {
        AbstractPositionItemView itemView = factory.get();
        itemView.setActivity( this );

        itemView.setCompany( workerEntry.getCompanyName() );
        itemView.setDepartment( workerEntry.getDepartment().getName() );
        itemView.setPosition( workerEntry.getPosition().getName() );
        //itemView.showMainInfo( workerEntry.isMain() );

        itemView.hideElements( true );

        return itemView;
    }

    @Inject
    AbstractEmployeePreviewView view;

    @Inject
    Provider< AbstractPositionItemView > factory;

    @Inject
    Lang lang;
}
