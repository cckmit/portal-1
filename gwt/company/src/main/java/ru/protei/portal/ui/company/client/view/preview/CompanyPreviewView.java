package ru.protei.portal.ui.company.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewActivity;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewView;

/**
 * Представление превью компании
 */
public class CompanyPreviewView extends Composite implements AbstractCompanyPreviewView {

    public CompanyPreviewView() {
        initWidget( ourUiBinder.createAndBindUi ( this ) );
    }

    @Override
    public void setActivity( AbstractCompanyPreviewActivity activity ) {
        this.activity = activity;
    }

    AbstractCompanyPreviewActivity activity;

    interface CompanyPreviewViewUiBinder extends UiBinder<HTMLPanel, CompanyPreviewView > {}
    private static CompanyPreviewViewUiBinder ourUiBinder = GWT.create( CompanyPreviewViewUiBinder.class );

}