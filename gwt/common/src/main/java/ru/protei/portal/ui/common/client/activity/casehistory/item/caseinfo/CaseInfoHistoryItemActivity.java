package ru.protei.portal.ui.common.client.activity.casehistory.item.caseinfo;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.CommentAndHistoryEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class CaseInfoHistoryItemActivity implements Activity, AbstractCaseInfoHistoryItemActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
        dialogView.setHeader(lang.caseDescriptionHistoryPreview());
        dialogView.saveButtonVisibility().setVisible(false);
        dialogView.addStyleName("modal-xl");
    }

    @Event
    public void onShowCaseInfoChanges(CommentAndHistoryEvents.ShowCaseInfoChanges event) {
        view.setDescription(event.caseInfoChanges);
        dialogView.getBodyContainer().add(view.asWidget());
        dialogView.showPopup();
    }

    @Override
    public void onSaveClicked() { dialogView.hidePopup(); }

    @Override
    public void onCancelClicked() { dialogView.hidePopup(); }

    @Inject
    Lang lang;
    @Inject
    AbstractCaseInfoHistoryItemView view;
    @Inject
    AbstractDialogDetailsView dialogView;
}
