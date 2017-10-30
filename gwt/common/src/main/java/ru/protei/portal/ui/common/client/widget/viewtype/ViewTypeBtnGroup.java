package ru.protei.portal.ui.common.client.widget.viewtype;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.ToggleButton;

/**
 * Переключатель представлений списков
 */
public class ViewTypeBtnGroup extends ToggleBtnGroup< ViewType > {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();

        for(ViewType type: ViewType.values()) {
            ToggleButton itemView = addBtn( type == ViewType.LIST? lang.list(): lang.table(), type, "btn btn-white" );
            itemView.setIcon(type.getIcon(), true);
        }
    }

    @Inject
    Lang lang;

}