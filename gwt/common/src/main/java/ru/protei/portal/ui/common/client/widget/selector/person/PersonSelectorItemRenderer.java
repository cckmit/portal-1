package ru.protei.portal.ui.common.client.widget.selector.person;

import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;

public class PersonSelectorItemRenderer {
    private static final String FIRED_ICON_STYLE_NAME = "fa fa-ban ban m-r-5";

    public static SelectorItem<PersonShortView> makeMultipleSelectorItem(PersonShortView value, String elementHtml, boolean isSelected) {
        PopupSelectableItem<PersonShortView> item = new PopupSelectableItem<>();

        if (!CrmConstants.Employee.UNDEFINED.equals(value.getId()) && value.isFired()) {
            item.setIcon(FIRED_ICON_STYLE_NAME);
        }

        item.setElementHtml(elementHtml);
        item.setTitle( elementHtml );
        item.setSelected(isSelected);
        return item;
    }

    public static SelectorItem<PersonShortView> makeSingleSelectorItem( PersonShortView value, String elementHtml) {
        PopupSelectorItem<PersonShortView> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        item.setTitle( elementHtml );
        if(value!=null){
            item.setIcon( value.isFired() ? FIRED_ICON_STYLE_NAME : "" );
        }
        return item;
    }
}
