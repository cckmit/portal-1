package ru.protei.portal.ui.sitefolder.client.widget.selector.servergroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItemWithEdit;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import java.util.function.Consumer;

public class ServerGroupButtonSelector extends ButtonPopupSingleSelector<ServerGroup> {
    @Inject
    public void init(Lang lang) {
        setItemRenderer(value -> value == null ? defaultValue : value.getName());
        setAddButton(true, lang.siteFolderServerGroupAddGroup());
    }

    @Override
    protected SelectorItem<ServerGroup> makeSelectorItem(final ServerGroup serverGroup, String elementHtml) {
        PopupSelectorItemWithEdit<ServerGroup> item = new PopupSelectorItemWithEdit<>();

        item.setName(elementHtml);
        item.setValue(serverGroup);
        item.setEditable(serverGroup != null);
        item.setTitle(elementHtml);
        item.addEditHandler(event -> editHandler.accept(serverGroup));

        return item;
    }

    public void setEditHandler(Consumer<ServerGroup> editHandler) {
        this.editHandler = editHandler;
    }

    private Consumer<ServerGroup> editHandler;
}
