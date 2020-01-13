package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

/**
 * Селектор сотрудников домашней компании
 */
public class EmployeeButtonSelector extends ButtonPopupSingleSelector<PersonShortView>
{

    @Inject
    public void init(EmployeeModel employeeModel) {
        setAsyncModel(employeeModel);
        setFilter(personView -> !personView.isFired());
        setItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }

    @Override
    protected SelectorItem makeSelectorItem( PersonShortView value, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        if(value!=null){
            item.setIcon( value.isFired() ? "not-active" : "" );
            item.setIcon( value.isFired() ? "fa fa-ban ban" : "" );
        }
        return item;
    }

}
