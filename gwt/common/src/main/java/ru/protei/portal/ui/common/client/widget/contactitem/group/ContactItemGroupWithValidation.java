package ru.protei.portal.ui.common.client.widget.contactitem.group;

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
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContactItemGroupWithValidation extends Composite
        implements HasValue<List<ContactItem>>, HasValidable, HasEnabled {

    public ContactItemGroupWithValidation() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
    }

    @Override
    public List<ContactItem> getValue() {
        modelToView.forEach((viewItem, contactItem) -> {
            if (contactItem != null) {
                contactItem.modify(viewItem.getValue());
            }
        });
        return stream(modelToView.values()).filter(Objects::nonNull).filter(item -> isNotEmpty(item.value())).collect(Collectors.toList());
    }

    @Override
    public void setValue( List<ContactItem> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<ContactItem> values, boolean fireEvents ) {
        clear();
        this.items = values == null ? new ArrayList<>() : values;

        items.forEach(this::makeItemView);
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

        if (!isEnabled) {
            for (TextWithValidationItem item : modelToView.keySet()) {
                if (isEmpty(item.getValue())) {
                    item.removeFromParent();
                } else {
                    item.setEnabled(false);
                }
            }
        } else {
            for (TextWithValidationItem item : modelToView.keySet()) {
                item.setEnabled(true);
            }
            boolean isHasEmptyItem = modelToView.values().stream().anyMatch(item -> item == null || isEmpty(item.value()));
            if(!isHasEmptyItem) {
                addEmptyItem();
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ContactItem>> handler ) {
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

    public void setNewContactItem(Supplier<ContactItem> newContactItem) {
        this.newContactItem = newContactItem;
    }

    private TextWithValidationItem makeItemView(ContactItem value) {
        TextWithValidationItem textWithValidationItem = new TextWithValidationItem();
        textWithValidationItem.setValue( value == null? null : value.value() );
        textWithValidationItem.setPlaceholder(placeHolder);
        textWithValidationItem.setRegexp(regexp);
        textWithValidationItem.setStyleName(itemStyleName);
        textWithValidationItem.setNotNull(false);
        textWithValidationItem.getElement().setAttribute(DEBUG_ID_ATTRIBUTE ,DebugIds.GROUP.ITEM);
        textWithValidationItem.addCloseHandler(event -> {
            if ( itemContainer.getWidgetCount() == 1 ) {
                return;
            }
            itemContainer.remove( event.getTarget() );
            ContactItem remove = modelToView.remove( event.getTarget() );
            items.remove( remove );

            Optional<TextWithValidationItem> emptyItem = modelToView.entrySet().stream()
                    .filter(entry -> entry == null || isEmpty(entry.getKey().getValue()))
                    .map(entry -> entry.getKey()).findAny();
            if(emptyItem.isPresent()) {
                emptyItem.get().setFocus(true);
            } else {
                addEmptyItem().setFocus(true);
            }
        } );

        textWithValidationItem.addAddHandler(event -> {
            addEmptyItem();
        } );
        modelToView.put(textWithValidationItem, value != null ? value : newContactItem.get());
        itemContainer.add(textWithValidationItem);
        return textWithValidationItem;
    }

    private TextWithValidationItem addEmptyItem() {
        return makeItemView( null );
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
    private Supplier<ContactItem> newContactItem;

    private List<ContactItem> items = new ArrayList<>();
    private final Map<TextWithValidationItem, ContactItem> modelToView = new HashMap<>();

    interface PhoneGroupUiBinder extends UiBinder<HTMLPanel, ContactItemGroupWithValidation> {}
    private static final PhoneGroupUiBinder ourUiBinder = GWT.create(PhoneGroupUiBinder.class);
}