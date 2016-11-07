package ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentDataList;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.handler.ItemChangeHandler;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.item.AutoAddVCItemStatus;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentPair;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.item.AutoAddVCItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контейнер для пар значение-комментарий
 */
public class AutoAddVCList extends Composite implements ValueCommentDataList{

    public AutoAddVCList() {
        initWidget(ourUiBinder.createAndBindUi(this));
        addItem();
    }

    @Override
    public List<ValueCommentPair> getDataList(){
        return itemList.stream()
                .filter(pair -> !pair.value().getText().trim().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void setDataList(List<ValueCommentPair> dataList){
        if(dataList == null || dataList.size() == 0){
            clear();
            return;
        }

        dataList.stream().forEachOrdered(pair ->
                new AutoAddVCItem(
                        changeHandler,
                        pair.value().getText(),
                        pair.comment().getText()
                )
        );
    }

    private void clear(){
        itemList = new ArrayList<>();
        root.clear();
        addItem();
    }

    private void addItem(){
        List<AutoAddVCItem> itemList = (List<AutoAddVCItem>) this.itemList;

        AutoAddVCItem item = new AutoAddVCItem(changeHandler);
        if(!itemList.isEmpty())
            itemList.get(itemList.size() - 1).updateStatus(AutoAddVCItemStatus.FILLED);
        itemList.add(item);

        root.add(item);
        item.focused();
    }

    private void removeItem(AutoAddVCItem item){
        item.removeFromParent();
    }


    @UiField
    HTMLPanel root;

    List<? extends ValueCommentPair> itemList = new ArrayList<>();
    ItemChangeHandler changeHandler = new ItemChangeHandler() {
        @Override
        public void onAdd() {
            addItem();
        }

        @Override
        public void onRemove(AutoAddVCItem item) {
            removeItem(item);
        }
    };

    private static InputFieldUiBinder ourUiBinder = GWT.create(InputFieldUiBinder.class);
    interface InputFieldUiBinder extends UiBinder<HTMLPanel, AutoAddVCList> {}
}