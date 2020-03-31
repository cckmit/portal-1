package ru.protei.portal.ui.common.client.activity.pathitem;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.struct.PathItem;
import ru.protei.portal.ui.common.client.activity.pathitem.item.AbstractPathItemActivity;
import ru.protei.portal.ui.common.client.activity.pathitem.item.AbstractPathItemView;
import ru.protei.portal.ui.common.client.activity.pathitem.list.AbstractPathItemListActivity;
import ru.protei.portal.ui.common.client.activity.pathitem.list.AbstractPathItemListView;
import ru.protei.portal.ui.common.client.events.PathInfoEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PathItemActivity implements Activity, AbstractPathItemActivity, AbstractPathItemListActivity {

    @Event
    public void onShow(PathInfoEvents.ShowList event) {
        if (event.parent == null || event.data == null) {
            return;
        }

        AbstractPathItemListView listView = listFactory.get();
        event.parent.clear();
        event.parent.add(listView.asWidget());

        addNewItems(listView.getItemsContainer(), event.data);
    }

    @Override
    public void onChangePath(AbstractPathItemView item) {
        onChangeItem(item);
    }

    @Override
    public void onChangeDesc(AbstractPathItemView item) {
        onChangeItem(item);
    }

    private void addNewItems(HasWidgets parent, List<PathItem> data) {

        for (PathItem pathItem : data) {
            addNewItem(parent, pathItem, data);
        }

        addNewEmptyItem(parent, data);
    }

    private void addNewEmptyItem(HasWidgets parent, List<PathItem> data){
        addNewItem(parent, new PathItem(), data);
    }

    private void addNewItem(HasWidgets parent, PathItem pathItem, List<PathItem> data) {
        AbstractPathItemView item = createItemView();
        fillItemView(item, pathItem);
        parent.add(item.asWidget());
        viewToModel.put(item, new PathItemModel(parent, pathItem, data));
    }

    private AbstractPathItemView createItemView() {
        AbstractPathItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        return itemView;
    }

    private void fillItemView(AbstractPathItemView itemView, PathItem pathItem) {
        itemView.path().setText(pathItem.getPath());
        itemView.desc().setText(pathItem.getDesc());
    }

    private void onChangeItem(AbstractPathItemView item) {

        PathItemModel model = viewToModel.get(item);

        String prevPath = model.item.getPath();
        String prevDesc = model.item.getDesc();
        String newPath = item.path().getText().trim();
        String newDesc = item.desc().getText().trim();

        if (prevPath.equals(newPath) && prevDesc.equals(newDesc)) {
            return;
        }

        if (prevPath.isEmpty() && prevDesc.isEmpty()) {
            model.data.add(model.item);
            addNewEmptyItem(model.parent, model.data);
        } else if (newPath.isEmpty() && newDesc.isEmpty()) {
            AbstractPathItemView emptyItem = findEmptyItem(model.parent);
            if (emptyItem != null) {
                removeItem(item);
                emptyItem.focused();
                return;
            }
        }

        model.item.setPath(newPath);
        model.item.setDesc(newDesc);
    }

    private AbstractPathItemView findEmptyItem(HasWidgets parent) {
        for (Map.Entry<AbstractPathItemView, PathItemModel> entry : viewToModel.entrySet()) {
            PathItemModel model = entry.getValue();
            if (model.parent == parent && model.item.getPath().isEmpty() && model.item.getDesc().isEmpty()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void removeItem(AbstractPathItemView item) {
        item.asWidget().removeFromParent();
        PathItemModel model = viewToModel.get(item);
        model.data.remove(model.item);
        viewToModel.remove(item);
    }

    @Inject
    Provider<AbstractPathItemListView> listFactory;
    @Inject
    Provider<AbstractPathItemView> itemFactory;

    Map<AbstractPathItemView, PathItemModel> viewToModel = new HashMap<>();
}
