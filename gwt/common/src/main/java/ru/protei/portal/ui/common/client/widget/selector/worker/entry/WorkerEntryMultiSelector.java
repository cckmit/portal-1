package ru.protei.portal.ui.common.client.widget.selector.worker.entry;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class WorkerEntryMultiSelector extends InputPopupMultiSelector<WorkerEntryShortView> {

    @Inject
    public void init(WorkerEntryModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(WorkerEntryShortView::getPersonName);
    }

    @Override
    public void onUnload() {
        model.clear();
        super.onUnload();
    }

    private WorkerEntryModel model;
}
