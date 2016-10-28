package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ValueCommentEvents;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;
import ru.protei.portal.ui.common.client.view.valuecomment.item.ValueCommentItemView;

import java.util.ArrayList;
import java.util.List;

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
                AbstractValueCommentItemView itemView = itemFactory.get();
                itemView.setActivity( this );
                itemView.value().setText( data.value().getText() );
                itemView.comment().setText( data.comment().getText() );
            });
        }
    }

    @Override
    public void onCreateClicked() {
        List<AutoAddVCItem> itemList = (List<AutoAddVCItem>) this.itemList;

        AutoAddVCItem item = new AutoAddVCItem(changeHandler);
        if(!itemList.isEmpty())
            itemList.get(itemList.size() - 1).updateStatus(AutoAddVCItemStatus.FILLED);
        itemList.add(item);

        root.add(item);
        item.focused();
    }

    @Override
    public void onDeleteClicked(AbstractValueCommentItemView item) {

    }

    @Inject
    AbstractValueCommentListView view;

    @Inject
    Provider<ValueCommentItemView> itemFactory;
    List<? extends ValueComment> itemList = new ArrayList<>();
}
