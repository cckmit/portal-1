package ru.protei.portal.ui.common.client.widget.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class TextGroupWithValidation extends Composite
        implements HasValue<List<String>>, HasValidable, HasEnabled {

    public TextGroupWithValidation() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
    }

    @Override
    public List<String> getValue() {
        return new ArrayList<>(items);
    }

    @Override
    public void setValue( List<String> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<String> values, boolean fireEvents ) {
        clear();
        this.items = values == null ? new ArrayList<>() : values;

        items.forEach( this :: makeItemAndFillValue );
        addEmptyItem();

        if(fireEvents) {
            ValueChangeEvent.fire( this, this.items);
        }
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return modelToView.keySet().stream().allMatch(TextWithValidationItem::isValid);
    }

    private void clear() {
        itemContainer.clear();
        items.clear();
        modelToView.clear();
    }

    @Override
    public boolean isEnabled() {
        return !itemContainer.getStyleName().contains("disabled");
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        itemContainer.setStyleName("disabled", !isEnabled);
        for (TextWithValidationItem item : modelToView.keySet()) {
            item.setEnabled(isEnabled);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public void setItemStyleName(String itemStyle) {
        this.itemStyleName = itemStyle;
    }

    private void makeItemAndFillValue(String value) {
        TextWithValidationItem textWithValidationItem = new TextWithValidationItem();
        textWithValidationItem.setValue( value == null ? "" : value );
        textWithValidationItem.setPlaceholder(placeHolder);
        textWithValidationItem.setRegexp(regexp);
        textWithValidationItem.setStyleName(itemStyleName);
        textWithValidationItem.getElement().setAttribute(DEBUG_ID_ATTRIBUTE ,DebugIds.GROUP.ITEM);
        textWithValidationItem.addCloseHandler(event -> {
            if ( itemContainer.getWidgetCount() == 1 ) {
                return;
            }
            itemContainer.remove( event.getTarget() );
            String remove = modelToView.remove( event.getTarget() );
            items.remove( remove );
            boolean isHasEmptyItem = modelToView.values().stream().anyMatch(StringUtils::isNotEmpty);
            if(!isHasEmptyItem) {
                addEmptyItem();
            }
        } );

        textWithValidationItem.addAddHandler(event -> {
            addEmptyItem();
            items.add( textWithValidationItem.getValue() );
        } );

        modelToView.put(textWithValidationItem, value );
        itemContainer.add(textWithValidationItem);
    }

    private void addEmptyItem() {
        makeItemAndFillValue( null );
    }

    private void setTestAttributes() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.GROUP.ROOT);
        itemContainer.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.GROUP.CONTAINER);
    }

    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel itemContainer;
    @UiField
    @Inject
    Lang lang;

    private String placeHolder;
    private String regexp;
    private String itemStyleName;

    private List<String> items = new ArrayList<>();
    private final Map<TextWithValidationItem, String> modelToView = new HashMap<>();

    interface PhoneGroupUiBinder extends UiBinder<HTMLPanel, TextGroupWithValidation> {}
    private static final PhoneGroupUiBinder ourUiBinder = GWT.create(PhoneGroupUiBinder.class);
}