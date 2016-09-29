package ru.protei.portal.ui.crm.client.view.company;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.crm.client.activity.company.AbstractCompanyActivity;
import ru.protei.portal.ui.crm.client.activity.company.AbstractCompanyView;

/**
 * Created by turik on 27.09.16.
 */
public class CompanyView extends Composite implements AbstractCompanyView {

    public CompanyView() {
        initWidget (ourUiBinder.createAndBindUi (this));
    }

    public void setActivity(AbstractCompanyActivity activity) {
        this.activity = activity;
    }

    AbstractCompanyActivity activity;

    private static CompanyViewUiBinder ourUiBinder = GWT.create (CompanyViewUiBinder.class);
    interface CompanyViewUiBinder extends UiBinder<HTMLPanel, CompanyView> {}

}