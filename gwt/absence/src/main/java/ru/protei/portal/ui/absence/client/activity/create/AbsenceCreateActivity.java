package ru.protei.portal.ui.absence.client.activity.create;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;

public abstract class AbsenceCreateActivity implements AbstractAbsenceCreateActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Override
    public void onRemoveClicked() {

    }

    @Override
    public void onSaveClicked() {

    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractAbsenceCreateView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    AbsenceControllerAsync absenceController;
}
