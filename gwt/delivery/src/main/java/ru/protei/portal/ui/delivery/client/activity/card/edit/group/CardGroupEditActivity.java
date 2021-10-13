package ru.protei.portal.ui.delivery.client.activity.card.edit.group;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.CardEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class CardGroupEditActivity implements AbstractCardGroupEditActivity,
        AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        prepareDialog(dialogView);
    }

    @Event
    public void onShow(CardEvents.GroupEdit event) {
        // todo подготовить поля, селекторы, отметки о том где изменяем несколько разных значений
        dialogView.showPopup();
    }

    @Override
    public void onSaveClicked() {
        // todo сохранение
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void prepareDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(this);
        dialog.getBodyContainer().clear();
        dialog.getBodyContainer().add(cardGroupEditView.asWidget());
        dialog.setHeader(lang.cardGroupModify());
        dialog.removeButtonVisibility().setVisible(false);
    }

    @Inject
    Lang lang;

    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    AbstractCardGroupEditView cardGroupEditView;
}
