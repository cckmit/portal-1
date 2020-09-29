package ru.protei.portal.ui.common.client.activity.contactitem;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.ui.common.client.events.ContactItemEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Активити для работы {@link ContactItem} элемента и списка
 */
public abstract class ContactItemActivity implements Activity, AbstractContactItemListActivity, AbstractContactItemActivity {

    @Event
    public void onShow( ContactItemEvents.ShowList event ) {
        if(event.parent == null || event.data == null || event.types == null || event.types.isEmpty())
            return;

        AbstractContactItemListView listView = listFactory.get();
        event.parent.clear();
        event.parent.add(listView.asWidget());

        addNewItems(listView.getItemsContainer(), event.types, event.data);
    }

    @Override
    public void onChangeType(AbstractContactItemView item) {
        viewToModel.get(item)
                .contactItem.modify(item.type().getValue());
    }

    private void addNewItem(ContactItem ci, HasWidgets parent, List<En_ContactItemType> allowedTypes, List<ContactItem> dataList){
        AbstractContactItemView item = createItemView(allowedTypes);
        fillItemView(item, ci);

        parent.add(item.asWidget());
        viewToModel.put(item, new ContactItemModel(parent, dataList, allowedTypes, ci));
    }

    private void addNewItems(HasWidgets parent, List<En_ContactItemType> allowedTypes, List<ContactItem> dataList){
        for(ContactItem ci: dataList) {
            if (!allowedTypes.contains(ci.type()))
                continue;

            addNewItem(ci, parent, allowedTypes, dataList);
        }

        addNewEmptyItem(parent, allowedTypes, dataList);
    }

    private void addNewEmptyItem(HasWidgets parent, List<En_ContactItemType> allowedTypes, List<ContactItem> dataList){
        addNewItem(new ContactItem("", allowedTypes.get(0)), parent, allowedTypes, dataList);
    }

    private void removeItem(AbstractContactItemView item){
        item.asWidget().removeFromParent();
        ContactItemModel model = viewToModel.get(item);
        model.data.remove(model.contactItem);
        viewToModel.remove(item);
    }

    @Override
    public void onChangeValue(AbstractContactItemView item) {
        ContactItemModel model = viewToModel.get(item);

        String prevValue = model.contactItem.value();
        String newValue = item.value().getText().trim();

        if (newValue.equals(prevValue)) {
            return;
        }

        if (prevValue.isEmpty()) {
            model.data.add(model.contactItem);
            addNewEmptyItem(model.parent, model.allowedTypes, model.data);
        }
        else if (newValue.isEmpty()) {
            AbstractContactItemView emptyItem = findEmptyItem(model.parent);
            if(emptyItem != null) {
                removeItem(item); // delete current, leave empty
                emptyItem.focused();
                return;
            }
        }

        model.contactItem.modify(newValue);
    }

    private AbstractContactItemView findEmptyItem(HasWidgets parent){

        for(Map.Entry<AbstractContactItemView, ContactItemModel> entry: viewToModel.entrySet()){
            ContactItemModel model = entry.getValue();
            if(model.parent == parent && model.contactItem.value().isEmpty())
                return entry.getKey();
        }

        return null;
    }

    private AbstractContactItemView createItemView(List<En_ContactItemType> allowedTypes){
        AbstractContactItemView itemView = itemFactory.get();
        if(allowedTypes.size() != 1)
            itemView.fillTypeOptions(allowedTypes);
        else
            itemView.typeVisibility().setVisible(false);

        itemView.setActivity(this);
        return itemView;
    }

    private void fillItemView(AbstractContactItemView itemView, ContactItem ci){
        itemView.value().setText(ci.value());
        if(itemView.typeVisibility().isVisible())
            itemView.type().setValue(ci.type());
    }

    @Inject
    Provider<AbstractContactItemListView> listFactory;

    @Inject
    Provider<AbstractContactItemView> itemFactory;

    Map<AbstractContactItemView, ContactItemModel> viewToModel = new HashMap<>();

}
