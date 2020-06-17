package ru.protei.portal.ui.account.client.widget.casefilter.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.account.client.widget.casefilter.item.CaseFilterItem;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseFilterGroup extends Composite
        implements HasValue<List<CaseFilterShortView>>, HasEnabled, HasCloseHandlers<CaseFilterGroup> {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<CaseFilterShortView> getValue() {
        return value;
    }

    @Override
    public void setValue( List<CaseFilterShortView> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<CaseFilterShortView> values, boolean fireEvents ) {
        clear();
        this.value = values == null ? new ArrayList<>() : values;

        value.forEach( this :: makeItemAndFillValue );
        addEmptyItem();

        if(fireEvents) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    private void clear() {
        itemContainer.clear();
        value.clear();
    }

    @Override
    public boolean isEnabled() {
        return !itemContainer.getStyleName().contains("disabled");
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        itemContainer.setStyleName("disabled", !isEnabled);

        for (CaseFilterItem item : modelToView.keySet()) {
            item.setEnabled(isEnabled);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<CaseFilterShortView>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public HandlerRegistration addCloseHandler( CloseHandler<CaseFilterGroup> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    private void makeItemAndFillValue( CaseFilterShortView value ) {
        CaseFilterItem caseFilterItem = itemProvider.get();
        caseFilterItem.setValue( value );
        caseFilterItem.addCloseHandler( event -> {
            if ( itemContainer.getWidgetCount() == 1 ) {
                return;
            }
            itemContainer.remove( event.getTarget() );
            CaseFilterShortView remove = modelToView.remove( event.getTarget() );
            this.value.remove( remove );
        } );

        caseFilterItem.addAddHandler( event -> {
            addEmptyItem();
        } );

        modelToView.put( caseFilterItem, value );
        itemContainer.add( caseFilterItem );
    }

    private void addEmptyItem() {
        makeItemAndFillValue( null );
    }

    @UiField
    HTMLPanel itemContainer;
    @UiField
    Lang lang;

    @Inject
    Provider<CaseFilterItem> itemProvider;

    private Map<CaseFilterItem, CaseFilterShortView> modelToView = new HashMap<>();

    private List<CaseFilterShortView> value = new ArrayList<>();

    interface PersonCaseFilterGroupUiBinder extends UiBinder<HTMLPanel, CaseFilterGroup> {}
    private static PersonCaseFilterGroupUiBinder ourUiBinder = GWT.create(PersonCaseFilterGroupUiBinder.class);
}