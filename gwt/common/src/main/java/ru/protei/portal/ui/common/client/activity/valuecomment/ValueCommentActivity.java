package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ValueCommentEvents;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;
import ru.protei.portal.ui.common.client.view.valuecomment.item.ValueCommentItemView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bondarenko on 28.10.16.
 */
public abstract class ValueCommentActivity implements Activity, AbstractValueCommentListActivity, AbstractValueCommentItemActivity {

    @Event
    public void onShow( ValueCommentEvents.ShowList event ) {
        if(event.data == null)
            return;

        event.parent.clear();
        event.parent.add(listView.asWidget());
        listView.getItemsContainer().clear();

        model = event.data;
        viewToModel.clear();

        if(model.isEmpty())
            addNewEmptyItem();
        else
            model.forEach(this::addNewItem);
    }

    @Override
    public void onCreateClicked(AbstractValueCommentItemView prevItem) {
        prevItem.setFilled();
        ValueComment vc = viewToModel.get(prevItem);
        vc.comment = prevItem.comment().getText();
        vc.value = prevItem.value().getText();

        addNewEmptyItem();
    }

    @Override
    public void onDeleteClicked(AbstractValueCommentItemView item) {
        item.asWidget().removeFromParent();
        ValueComment vc = viewToModel.remove(item);
        model.remove(vc);
    }

    private void addNewEmptyItem(){
        ValueComment vc = new ValueComment("", "");
        model.add(vc);
        addNewItem(vc);
    }

    private void addNewItem(ValueComment vc){
        AbstractValueCommentItemView itemView = itemFactory.get();
        itemView.value().setText(vc.value);
        itemView.comment().setText(vc.comment);
        itemView.setActivity( this );
        itemView.setNew();
        listView.getItemsContainer().add(itemView.asWidget());
        itemView.focused();

        viewToModel.put(itemView, vc);
    }

    @Inject
    AbstractValueCommentListView listView;

    @Inject
    Provider<AbstractValueCommentItemView> itemFactory;


    List<ValueComment> model;
    Map<AbstractValueCommentItemView, ValueComment> viewToModel = new HashMap<>();
}
