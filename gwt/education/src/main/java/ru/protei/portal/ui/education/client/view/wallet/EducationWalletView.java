package ru.protei.portal.ui.education.client.view.wallet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.education.client.activity.wallet.AbstractEducationWalletActivity;
import ru.protei.portal.ui.education.client.activity.wallet.AbstractEducationWalletView;

public class EducationWalletView extends Composite implements AbstractEducationWalletView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEducationWalletActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCompanyName(String companyName) {
        this.companyName.setInnerText(companyName);
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName.setInnerText(departmentName);
    }

    @Override
    public void setCoins(Integer coins) {
        this.coins.setInnerText(String.valueOf(coins));
        this.coins.removeClassName("text-success");
        this.coins.removeClassName("text-danger");
        this.coins.addClassName(coins >= 0 ? "text-success" : "text-danger");
    }

    @Override
    public void setCountConference(Long count) {
        this.countConference.setInnerText(String.valueOf(count));
        this.countConference.removeClassName("text-success");
        this.countConference.removeClassName("text-warning");
        this.countConference.addClassName(count > 0 ? "text-success" : "text-warning");
    }

    @Override
    public void setCountCourse(Long count) {
        this.countCourse.setInnerText(String.valueOf(count));
        this.countCourse.removeClassName("text-success");
        this.countCourse.removeClassName("text-warning");
        this.countCourse.addClassName(count > 0 ? "text-success" : "text-warning");
    }

    @Override
    public void setCountLiterature(Long count) {
        // nobody cares
    }

    @UiField
    DivElement companyName;
    @UiField
    DivElement departmentName;
    @UiField
    HeadingElement coins;
    @UiField
    SpanElement countConference;
    @UiField
    SpanElement countCourse;

    private AbstractEducationWalletActivity activity;

    interface EducationWalletViewBinder extends UiBinder<HTMLPanel, EducationWalletView> {}
    private static EducationWalletViewBinder ourUiBinder = GWT.create(EducationWalletViewBinder.class);
}
