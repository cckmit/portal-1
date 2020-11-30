package ru.protei.portal.ui.plan.client.activity.preview;

import ru.protei.portal.core.model.view.CaseShortView;

public interface AbstractPlanPreviewActivity {
    void onFullScreenPreviewClicked();
    void onGoToPlansClicked();
    void onItemClicked(CaseShortView value);
}
