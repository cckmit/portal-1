package ru.protei.portal.ui.common.client.widget.viewtype;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

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
            addBtnWithIcon(
                    type.getIcon(),
                    "btn btn-white btn-without-border eq-type",
                    type == ViewType.LIST? lang.list(): lang.table(),
                    type
            );
        }
    }

    @Inject
    Lang lang;

}