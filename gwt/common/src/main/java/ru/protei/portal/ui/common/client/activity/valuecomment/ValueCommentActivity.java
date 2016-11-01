package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.ValueCommentEvents;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;
import ru.protei.portal.ui.common.client.view.valuecomment.list.ValueCommentListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Активити элемента и списка
 */
public abstract class ValueCommentActivity implements Activity, AbstractValueCommentListActivity, AbstractValueCommentItemActivity {

    @Event
    public void onShow( ValueCommentEvents.ShowList event ) {
        if(event.data == null)
            return;

        AbstractValueCommentListView listView = new ValueCommentListView();
        event.parent.clear();
        event.parent.add(listView.asWidget());

        List<ValueComment> dataList = event.data;

        if(dataList.isEmpty())
            addNewEmptyItem(listView.getItemsContainer(), dataList);
        else
            addNewItems(listView.getItemsContainer(), dataList);
    }

    @Override
    public void onChangeComment(AbstractValueCommentItemView item) {
        onChangeValueComment(item);
    }

    @Override
    public void onChangeValue(AbstractValueCommentItemView item) {
        onChangeValueComment(item);
    }


    private void addNewItem(ValueComment vc, HasWidgets parent, List<ValueComment> dataList){
        AbstractValueCommentItemView item = createItemView(vc);
        dataList.add(vc);
        parent.add(item.asWidget());
        viewToModel.put(item, new ValueCommentModel(parent, dataList, vc));
    }

    private void addNewItems(HasWidgets parent, List<ValueComment> dataList){
        dataList.forEach(vc -> addNewItem(vc, parent, dataList));
    }

    private void addNewEmptyItem(HasWidgets parent, List<ValueComment> dataList){
        addNewItem(new ValueComment("",""), parent, dataList);
    }

    private void removeItem(AbstractValueCommentItemView item){
        item.asWidget().removeFromParent();
        ValueComment vc = viewToModel.get(item).valueComment;
        viewToModel.get(item).data.remove(vc);
        viewToModel.remove(item);
    }

    private void onChangeValueComment(AbstractValueCommentItemView item){
        ValueCommentModel model = viewToModel.get(item);

        String prevValue = model.valueComment.value;
        String prevComment = model.valueComment.comment;
        String newValue = item.value().getText().trim();
        String newComment = item.comment().getText().trim();


        if(newValue.equals(prevValue) && newComment.equals(prevComment))
            return;

        if((prevComment + prevValue).isEmpty())
            addNewEmptyItem(model.parent, model.data);

        else if((newValue + newComment).isEmpty() && model.data.size()!=1){
            AbstractValueCommentItemView emptyItem = findEmptyItem(model.data);
            if(emptyItem != null) {
                removeItem(item); // delete current, leave empty
                emptyItem.focused();
                return;
            }
        }

        model.valueComment.value = newValue;
        model.valueComment.comment = newComment;
    }

    private AbstractValueCommentItemView findEmptyItem(List<ValueComment> dataList){
        Optional<Map.Entry<AbstractValueCommentItemView, ValueCommentModel>> result = viewToModel
                .entrySet()
                .stream()
                .filter(e -> {
                    ValueCommentModel model = e.getValue();
                    return model.data == dataList && (model.valueComment.value + model.valueComment.comment).isEmpty();
                })
                .findFirst();

        if(result.isPresent())
            return result.get().getKey();
        else
            return null;

//        return dataList.stream().anyMatch(vc -> (vc.comment + vc.value).isEmpty());
    }

    private AbstractValueCommentItemView createItemView(ValueComment vc){
        AbstractValueCommentItemView itemView = itemFactory.get();
        itemView.setActivity( this );
        itemView.value().setText(vc.value);
        itemView.comment().setText(vc.comment);
        return itemView;
    }

//    @Inject
//    Provider<AbstractValueCommentListView> listFactory;

    @Inject
    Provider<AbstractValueCommentItemView> itemFactory;

    Map<AbstractValueCommentItemView, ValueCommentModel> viewToModel = new HashMap<>();

}
