package ru.protei.portal.ui.common.client.view.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.activity.loading.AbstractLoadingActivity;
import ru.protei.portal.ui.common.client.activity.loading.AbstractLoadingView;

/**
 * Виджет загрузки
 */
public class LoadingView extends Composite implements AbstractLoadingView {

    public LoadingView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractLoadingActivity activity ) {
        this.activity = activity;
    }

    AbstractLoadingActivity activity;

    interface LoadingViewUiBinder extends UiBinder<Widget, LoadingView> {}
    private static LoadingViewUiBinder ourUiBinder = GWT.create( LoadingViewUiBinder.class );

}