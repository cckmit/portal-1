package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;

/**
 * Представление должности
 */
public class PositionItemView extends Composite implements AbstractPositionItemView {

    public PositionItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractPositionItemActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasVisibility departmentContainerVisibility() {
        return departmentContainer;
    }

    @Override
    public HasVisibility departmentHeadContainerVisibility() {
        return departmentHeadContainer;
    }

    @Override
    public void setDepartment( String department ) {
        this.department.setInnerText( department );
    }

    @Override
    public void setDepartmentParent(String departmentParent) {
        this.departmentParent.setInnerText(departmentParent);
    }

    @Override
    public void setPosition( String position ) {
        this.position.setInnerText( position );
    }

    @Override
    public void setDepartmentHead(String departmentHead, String link) {
        this.departmentHead.setHref(link);
        this.departmentHead.setInnerText(departmentHead);
    }

    @UiField
    SpanElement departmentParent;

    @UiField
    SpanElement department;

    @UiField
    SpanElement position;

    @UiField
    AnchorElement departmentHead;

    @UiField
    HTMLPanel departmentContainer;

    @UiField
    HTMLPanel departmentHeadContainer;

    AbstractPositionItemActivity activity;

    private static PositionItemViewUiBinder ourUiBinder = GWT.create( PositionItemViewUiBinder.class );
    interface PositionItemViewUiBinder extends UiBinder< HTMLPanel, PositionItemView > {}
}