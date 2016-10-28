package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ValueCommentEvents;

/**
 * Created by bondarenko on 28.10.16.
 */
public abstract class ValueCommentListActivity implements Activity, AbstractValueCommentListActivity, AbstractValueCommentItemActivity {

    @Event
    public void onShow( ValueCommentEvents.ShowList event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        if (event.data != null) {
            event.data.forEach((data)->{
                AVCIV itemView = factory.get();
                AbstractValueCommentItemView itemV =
                itemView.setActivity( this );
                itemView.setValue( data.value() );
                itemView.setComment( data.comment() );
            });
        }
    }

    @Override
    public void onCreateClicked(AbstractValueCommentItemView item) {

    }

    @Override
    public void onDeleteClicked(AbstractValueCommentItemView item) {

    }

    @Inject
    AbstractValueCommentListView view;
}
