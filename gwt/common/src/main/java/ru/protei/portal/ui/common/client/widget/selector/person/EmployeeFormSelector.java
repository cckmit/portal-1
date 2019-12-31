package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.components.client.form.FormSingleSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.item.PopupSelectorItem;

/**
 * Селектор сотрудников домашней компании
 */
public class EmployeeFormSelector extends FormSingleSelector<PersonShortView>
{

    @Inject
    public void init(EmployeeModel employeeModel) {
        setAsyncSelectorModel(employeeModel);
        setFilter(personView -> !personView.isFired());
        setSelectorItemRenderer( value -> value == null ? defaultValue : value.getName() );
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

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
