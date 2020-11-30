package ru.protei.portal.ui.education.client.view.admin.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.ui.education.client.activity.admin.filter.AbstractEducationAdminFilterActivity;
import ru.protei.portal.ui.education.client.activity.admin.filter.AbstractEducationAdminFilterView;

public class EducationAdminFilterView extends Composite implements AbstractEducationAdminFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        resetFilter();
    }

    @Override
    public void setActivity(AbstractEducationAdminFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        showOnlyNotApproved.setValue(true);
        showOutdated.setValue(false);
    }

    @Override
    public HasValue<Boolean> showOnlyNotApproved() {
        return showOnlyNotApproved;
    }

    @Override
    public HasValue<Boolean> showOutdated() {
        return showOutdated;
    }

    @UiHandler("showOnlyNotApproved")
    public void showOnlyNotApprovedClicked(ClickEvent event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("showOutdated")
    public void showOutdatedClicked(ClickEvent event) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiField
    CheckBox showOnlyNotApproved;
    @UiField
    CheckBox showOutdated;

    private AbstractEducationAdminFilterActivity activity;

    interface EducationAdminFilterViewUiBinder extends UiBinder<HTMLPanel, EducationAdminFilterView> {}
    private static EducationAdminFilterViewUiBinder outUiBinder = GWT.create(EducationAdminFilterViewUiBinder.class);
}
