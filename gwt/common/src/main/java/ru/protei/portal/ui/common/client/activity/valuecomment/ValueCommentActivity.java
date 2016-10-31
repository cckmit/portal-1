package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ValueCommentEvents;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;

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

        AbstractValueCommentListView listView = listFactory.get();
        event.parent.clear();
        event.parent.add(listView.asWidget());

        List<ValueComment> model = event.data;
        HasWidgets parent = listView.getItemsContainer();

        parentToModelList.put(parent, model);

        if(model.isEmpty())
            addNewEmptyItem(parent);
        else
            model.forEach(vc -> addNewItem(parent, vc));
    }

    @Override
    public void onCreateClicked(AbstractValueCommentItemView prevItem) {
        prevItem.setFilled();
        ValueComment vc = viewToModel.get(prevItem);
        vc.comment = prevItem.comment().getText();
        vc.value = prevItem.value().getText();

        HasWidgets parent = modelToParent.get(viewToModel.get(prevItem));
        addNewEmptyItem(parent);
    }

    @Override
    public void onDeleteClicked(AbstractValueCommentItemView item) {
        item.asWidget().removeFromParent();

        ValueComment vc = viewToModel.remove(item);
        HasWidgets parent = modelToParent.remove(vc);
        parentToModelList.get(parent).remove(vc);
    }

    private void addNewEmptyItem(HasWidgets listView){
        ValueComment vc = new ValueComment("", "");
        modelToParent.put(vc, listView);
        parentToModelList.get(listView).add(vc);

        addNewItem(listView, vc);
    }

    private void addNewItem(HasWidgets listView, ValueComment vc){
        AbstractValueCommentItemView itemView = itemFactory.get();
        itemView.value().setText(vc.value);
        itemView.comment().setText(vc.comment);
        itemView.setActivity( this );
        itemView.setNew();
        listView.add(itemView.asWidget());
        itemView.focused();
        viewToModel.put(itemView, vc);
    }

    @Inject
    Provider<AbstractValueCommentListView> listFactory;

    @Inject
    Provider<AbstractValueCommentItemView> itemFactory;


    Map<AbstractValueCommentItemView, ValueComment> viewToModel = new HashMap<>();
    Map<ValueComment, HasWidgets> modelToParent = new HashMap<>();
    Map<HasWidgets, List<ValueComment>> parentToModelList = new HashMap<>();
}
