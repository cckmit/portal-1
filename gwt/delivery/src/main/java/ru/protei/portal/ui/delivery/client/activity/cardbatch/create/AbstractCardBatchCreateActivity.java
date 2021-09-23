package ru.protei.portal.ui.delivery.client.activity.cardbatch.create;

import ru.protei.portal.core.model.ent.CaseState;

import java.util.function.Consumer;

public interface AbstractCardBatchCreateActivity {

    void onSaveClicked();

    void onCancelClicked();

    void getCaseState(Long id, Consumer<CaseState> success);
}
