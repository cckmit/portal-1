package ru.protei.portal.ui.common.client.widget.selector.companydepartment;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.EditHandler;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItemWithEdit;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.Refreshable;

import static ru.protei.portal.core.model.helper.CollectionUtils.contains;

/**
 * Селектор списка отделов
 */
public class CompanyDepartmentSelector extends ButtonPopupSingleSelector<EntityOption> implements Refreshable {
    @Inject
    public void init(CompanyDepartmentModel companyModel) {
        this.model = companyModel;
        setModel(companyModel);
        setItemRenderer(value -> value == null ? defaultValue : value.getDisplayText());
    }

    @Override
    protected SelectorItem makeSelectorItem(EntityOption value, String elementHtml) {
        PopupSelectorItemWithEdit item = new PopupSelectorItemWithEdit();
        item.setName(elementHtml);
        item.setId(value.getId());

        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setId(value.getId());
        item.addEditHandler(editHandler);
        return item;
    }

    @Override
    public void refresh() {
        EntityOption value = getValue();
        if (value != null
                && !contains( model.getValues(), value )) {
            setValue( null );
        }
    }

    public void updateCompanyDepartments(Long companyId) {
        if(model!=null){
            model.updateCompanyDepartments(this, companyId);
        }
    }

    public void setEditHandler(EditHandler editHandler){
        this.editHandler = editHandler;
    }

    private CompanyDepartmentModel model;
    private EditHandler editHandler;
}
