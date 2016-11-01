package ru.protei.portal.ui.common.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.notify.AbstractNotifyView;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.view.dialogdetails.DialogDetailsView;
import ru.protei.portal.ui.common.client.models.CompanyModel;
import ru.protei.portal.ui.common.client.view.notify.NotifyView;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

/**
 * Описание классов фабрики
 */
public class CommonClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind( AbstractDialogDetailsView.class ).to( DialogDetailsView.class ).in( Singleton.class );

        bind(NotifyActivity.class).asEagerSingleton();
        bind( AbstractNotifyView.class ).to(NotifyView.class);

        bind( CompanyModel.class ).asEagerSingleton();

        requestStaticInjection( RequestCallback.class );
    }
}

