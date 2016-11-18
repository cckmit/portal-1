package ru.protei.portal.ui.common.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.common.client.activity.actionbar.ActionBarActivity;
import ru.protei.portal.ui.common.client.activity.notify.AbstractNotifyView;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentItemView;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentListView;
import ru.protei.portal.ui.common.client.activity.valuecomment.ValueCommentActivity;
import ru.protei.portal.ui.common.client.view.notify.NotifyView;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.view.dialogdetails.DialogDetailsView;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.client.view.valuecomment.item.ValueCommentItemView;
import ru.protei.portal.ui.common.client.view.valuecomment.list.ValueCommentListView;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemView;
import ru.protei.winter.web.common.client.view.section.SectionItemView;

/**
 * Описание классов фабрики
 */
public class CommonClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( ActionBarActivity.class ).asEagerSingleton();
        bind( AbstractSectionItemView.class ).to( SectionItemView.class );

        bind( AbstractDialogDetailsView.class ).to( DialogDetailsView.class ).in( Singleton.class );

        bind( NotifyActivity.class ).asEagerSingleton();
        bind( AbstractNotifyView.class ).to( NotifyView.class );

        bind( ValueCommentActivity.class ).asEagerSingleton();
        bind( AbstractValueCommentListView.class ).to( ValueCommentListView.class );
        bind( AbstractValueCommentItemView.class ).to( ValueCommentItemView.class );
        
        bind( CompanyModel.class ).asEagerSingleton();

        bind( DateFormatter.class ).in( Singleton.class );

        requestStaticInjection( RequestCallback.class );

    }
}

