package ru.protei.portal.ui.company.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyListActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;

/**
 * Вид формы компания
 */
public class CompanyItemView extends Composite implements AbstractCompanyItemView {

    private static CompanyItemViewUiBinder ourUiBinder = GWT.create (CompanyItemViewUiBinder.class);

    public void setActivity(AbstractCompanyListActivity activity) {
        this.activity = activity;
    }

    public void setName(String name) {
        this.name.setInnerText (name);
    }

    public void setType(String type) {
        this.type.setInnerText (type);
    }

    AbstractCompanyListActivity activity;

    @UiField
    DivElement name;
    @UiField
    DivElement type;

    interface CompanyItemViewUiBinder extends UiBinder<HTMLPanel, CompanyItemView> {}
    public CompanyItemView() {
        initWidget (ourUiBinder.createAndBindUi (this));
    }
}