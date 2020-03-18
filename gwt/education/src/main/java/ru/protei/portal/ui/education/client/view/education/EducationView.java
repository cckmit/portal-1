package ru.protei.portal.ui.education.client.view.education;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.education.client.activity.education.AbstractEducationActivity;
import ru.protei.portal.ui.education.client.activity.education.AbstractEducationView;

public class EducationView extends Composite implements AbstractEducationView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEducationActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets container() {
        return container;
    }

    @Override
    public HasVisibility toggleButtonVisibility() {
        return toggleButton;
    }

    @UiHandler("toggleButton")
    public void toggleButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onToggleViewClicked();
        }
    }

    @UiHandler("reloadButton")
    public void reloadButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onReloadClicked();
        }
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel container;
    @UiField
    Button toggleButton;
    @UiField
    Button reloadButton;

    private AbstractEducationActivity activity;

    interface EducationViewBinder extends UiBinder<HTMLPanel, EducationView> {}
    private static EducationViewBinder ourUiBinder = GWT.create(EducationViewBinder.class);
}
