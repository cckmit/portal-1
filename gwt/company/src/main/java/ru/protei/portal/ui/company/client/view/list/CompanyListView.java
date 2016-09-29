package ru.protei.portal.ui.company.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListView;

/**
 * Created by turik on 27.09.16.
 */
public class CompanyListView extends Composite implements AbstractCompanyListView {

    public CompanyListView() {
        initWidget (ourUiBinder.createAndBindUi (this));
    }

    public void setActivity(AbstractCompanyListActivity activity) {
        this.activity = activity;
    }

    AbstractCompanyListActivity activity;

    private static CompanyViewUiBinder ourUiBinder = GWT.create (CompanyViewUiBinder.class);
    interface CompanyViewUiBinder extends UiBinder<HTMLPanel, CompanyListView > {}

}