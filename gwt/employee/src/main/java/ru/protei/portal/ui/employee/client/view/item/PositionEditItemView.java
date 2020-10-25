package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionEditItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionEditItemView;

public class PositionEditItemView extends Composite implements AbstractPositionEditItemView {
    public PositionEditItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }


    @Override
    public void setActivity(AbstractPositionEditItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setDepartment(String department) {
        this.department.setInnerText( department );
    }

    @Override
    public void setPosition(String position) {
        this.position.setInnerText( position );
    }

    @Override
    public void setCompany(String position) {
        this.company.setInnerText( position );
    }

    @Override
    public void setContractAgreement(Boolean isContractAgreement) {
        contractAgreement.setInnerText(isContractAgreement != null && isContractAgreement.equals(true) ? lang.yes() : lang.no());
    }

    @Override
    public void setRemovePositionEnable (boolean isEnable) {
        removePosition.setStyleName("link-disabled", !isEnable);
    }

    @UiHandler("removePosition")
    public void onRemovePositionClicked (ClickEvent event){
        event.preventDefault();
        if (activity != null){
            activity.onRemovePositionClicked(this);
        }
    }


    @UiField
    SpanElement department;

    @UiField
    SpanElement position;

    @UiField
    SpanElement company;

    @UiField
    SpanElement contractAgreement;

    @UiField
    Anchor removePosition;

    @UiField
    Lang lang;

    private AbstractPositionEditItemActivity activity;

    private static PositionEditItemUiBinder ourUiBinder = GWT.create(PositionEditItemUiBinder.class);
    interface PositionEditItemUiBinder extends UiBinder<HTMLPanel, PositionEditItemView> {}
}