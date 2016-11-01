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
 * Активити элемента и списка
 */
public abstract class ValueCommentActivity implements Activity, AbstractValueCommentListActivity, AbstractValueCommentItemActivity {

    @Event
    public void onShow( ValueCommentEvents.ShowList event ) {
        if(event.data == null)
            return;

//        AbstractValueCommentListView listView = new ValueCommentListView();
        AbstractValueCommentListView listView = listFactory.get();
        event.parent.clear();
        event.parent.add(listView.asWidget());

        List<ValueComment> dataList = event.data;

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
        if(dataList.isEmpty())
            addNewEmptyItem(parent, dataList);
        else
            dataList.forEach(vc -> addNewItem(vc, parent, dataList));
    }

    private void addNewEmptyItem(HasWidgets parent, List<ValueComment> dataList){
        addNewItem(new ValueComment("",""), parent, dataList);
    }

    private void removeItem(AbstractValueCommentItemView item){
        item.asWidget().removeFromParent();
        ValueCommentModel model = viewToModel.get(item);
        model.data.remove(model.valueComment);
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

        if(prevComment.isEmpty() && prevValue.isEmpty())
            addNewEmptyItem(model.parent, model.data);

        else if(newValue.isEmpty() && newComment.isEmpty() && model.data.size()!=1){
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

        for(Map.Entry<AbstractValueCommentItemView, ValueCommentModel> entry: viewToModel.entrySet()){
            ValueCommentModel model = entry.getValue();
            if(model.data == dataList && model.valueComment.value.isEmpty() && model.valueComment.comment.isEmpty())
                return entry.getKey();
        }

        return null;
    }

    private AbstractValueCommentItemView createItemView(ValueComment vc){
        AbstractValueCommentItemView itemView = itemFactory.get();
        itemView.setActivity( this );
        itemView.value().setText(vc.value);
        itemView.comment().setText(vc.comment);
        return itemView;
    }

    @Inject
    Provider<AbstractValueCommentListView> listFactory;

    @Inject
    Provider<AbstractValueCommentItemView> itemFactory;

    Map<AbstractValueCommentItemView, ValueCommentModel> viewToModel = new HashMap<>();

}
