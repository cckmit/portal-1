package ru.protei.portal.ui.common.client.widget.selector.workerposition;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.WorkerPosition;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;
import ru.protei.portal.ui.common.client.service.WorkerPositionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.person.Refreshable;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Модель селектора должностей
 */
public abstract class WorkerPositionModel implements Activity, SelectorModel<EntityOption> {

    public void updateWorkerPositions(Refreshable selector, Long companyId) {
        workerPositionController.getWorkerPositions(companyId, new FluentCallback<List<WorkerPosition>>()
                .withSuccess(workerPositions -> {
                    fillEntityOptionList(workerPositions);
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

    private void fillEntityOptionList (List<WorkerPosition> workerPositions) {
        options.clear();
        workerPositions.forEach(workerPosition -> options.add (new EntityOption(workerPosition.getName(), workerPosition.getId())));
    }


    @Inject
    WorkerPositionControllerAsync workerPositionController;

    private List<EntityOption> options = new ArrayList<>();
}