package ru.protei.portal.ui.delivery.client.activity.cardbatch.edit;

import ru.protei.portal.core.model.ent.CaseState;

import java.util.function.Consumer;

public interface AbstractCardBatchEditActivity {

    void getCaseState(Long id, Consumer<CaseState> success);

    void onBackClicked();

    void onCommonInfoEditClicked();
}
