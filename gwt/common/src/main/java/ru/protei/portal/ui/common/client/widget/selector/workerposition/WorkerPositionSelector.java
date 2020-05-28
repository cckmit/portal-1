package ru.protei.portal.ui.common.client.widget.selector.workerposition;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.EditHandler;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItemWithEdit;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.Refreshable;

/**
 * Селектор списка отделов
 */
public class WorkerPositionSelector extends ButtonPopupSingleSelector<EntityOption> implements Refreshable {

    @Inject
    public void init(WorkerPositionModel positionModel) {
        this.model = positionModel;
        setModel(positionModel);
        setItemRenderer(value -> value == null ? lang.selectValue() : value.getDisplayText());

    }

    @Override
    protected SelectorItem<EntityOption> makeSelectorItem(EntityOption value, String elementHtml) {
        PopupSelectorItemWithEdit<EntityOption> item = new PopupSelectorItemWithEdit<>();

        item.setName(elementHtml);
        item.setId(value.getId());
        item.addEditHandler(editHandler);
        return item;
    }

    @Override
    public void refresh() {
        EntityOption value = getValue();
        boolean isValueContains = false;
        if (value != null){
            for (EntityOption modelValue : model.getValues()) {
                if (modelValue.getId().equals(value.getId())){
                    isValueContains = true;
                    setValue(modelValue);
                    break;
                }
            }
            if (!isValueContains){
                setValue(null);
            }
        }
    }

    @Override
    public void onShowPopupClicked( ClickEvent event) {
        if (companyId != null) {
            super.onShowPopupClicked(event);
            checkNoElements();
        } else {
            ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem item = new ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem();
            item.setName(lang.initiatorSelectACompany());
            item.getElement().addClassName(UiConstants.Styles.TEXT_CENTER);
            getPopup().getChildContainer().add(item);
            getPopup().showNear( button );
        }
    }

    public void reload() {
        updateWorkerPositions(companyId);
        getPopup().hide();
    }

    public void updateWorkerPositions(Long companyId) {
        this.companyId = companyId;
        if(model!=null){
            model.updateWorkerPositions(this, companyId);
        }
    }

    public void setAddButtonVisible (boolean isVisible){
        setAddButtonVisibility(isVisible);
        setAddButton(isVisible, lang.positionAddButton());
    }

    public void setEditHandler(EditHandler editHandler){
        this.editHandler = editHandler;
    }

    public void setAddHandler (AddHandler handler){
        addAddHandler(handler);
    }

    @Inject
    Lang lang;

    private WorkerPositionModel model;
    private EditHandler editHandler;
    private Long companyId;
}
