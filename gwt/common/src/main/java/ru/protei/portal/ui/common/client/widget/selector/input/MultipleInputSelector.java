package ru.protei.portal.ui.common.client.widget.selector.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.widget.selector.base.MultipleSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный селектор с полем ввода
 */
public class MultipleInputSelector<T> extends MultipleSelector<T> implements HasEnabled {

    public MultipleInputSelector() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initHandlers();
    }

    public void setHeader( String label ) {
        this.label.removeStyleName("hide");
        this.label.getElement().setInnerText( label == null ? "" : label );
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        isEnabled = enabled;
        if ( isEnabled ) {
            itemContainer.removeStyleName( "inactive" );
        } else {
            itemContainer.addStyleName( "inactive" );
        }
        caretButton.setEnabled( isEnabled );
        clearButton.setEnabled( isEnabled );
        itemViews.forEach((v) -> v.setEnabled(isEnabled));
    }

    @UiHandler( { "caretButton" } )
    public void onShowPopupClicked( ClickEvent event ) {
        if (!isEnabled) {
            return;
        }
        showPopup( itemContainer );
    }

    @UiHandler( { "clearButton" } )
    public void onClearClicked( ClickEvent event ) {
        if (!isEnabled) {
            return;
        }
        clearValues(true );
    }

    @Override
    protected void onUserCanAddMoreItems(boolean isCanAdd) {
        caretButton.setVisible(isCanAdd);
    }

    @Override
    public void fillSelectorView(List<T> selectedItems) {
        itemContainer.clear();
        itemViews.clear();
        selectedItems.forEach(this::addItem);
        clearButton.setVisible(!selectedItems.isEmpty());
    }

    @Override
    public void fillOptions( List<T> options ) {
        clearOptions();
        for ( T option : options ) {
            addOption( String.valueOf( option ), option );
        }
    }

    public void setAddName( String text ) {
        setAddName(null, text);
    }

    public void setAddName( String icon, String text ) {
        if (icon != null) {
            addIcon.setClassName("fa " + icon);
        } else {
            addIcon.setClassName("hide");
        }
        add.setInnerText(text);
        add.removeClassName("caret");
    }

    public void setClearName( String text ) {
        setClearName(null, text);
    }

    public void setClearName( String icon, String text ) {
        if (icon != null) {
            clearIcon.setClassName("fa " + icon);
        } else {
            clearIcon.setClassName("hide");
        }
        clear.setInnerText(text);
        clear.setClassName("");
    }

    private void clearValues(boolean fireEvents) {
        itemContainer.clear();
        itemViews.clear();
        clearSelected();
        clearButton.setVisible(false);
        if ( fireEvents ) {
            ValueChangeEvent.fire(this, null);
        }
    }

    private void addItem(T item) {
        SelectItemView itemView = itemViewProvider.get();
        itemView.setValue(getNameForItem(item));
        itemView.setEnabled(isEnabled);

        itemViews.add( itemView );
        itemView.setActivity( itemViewToRemove -> removeItem( itemViewToRemove, item ) );
        itemContainer.add( itemView );
    }

    private void removeItem( SelectItemView itemView, T item ) {
        itemContainer.remove( itemView );
        itemViews.remove( itemView );

        setItemChecked(item, false);
    }

    private void initHandlers() {
        itemContainer.addDomHandler( event -> {
            if (!isEnabled) {
                return;
            }
            showPopup( itemContainer );
        }, ClickEvent.getType() );
    }

    public void setAddEnsureDebugId(String debugId) {
        caretButton.ensureDebugId(debugId);
    }

    public void setClearEnsureDebugId(String debugId) {
        clearButton.ensureDebugId(debugId);
    }

    @UiField
    Button caretButton;
    @UiField
    HTMLPanel label;
    @UiField
    HTMLPanel itemContainer;
    @UiField
    SpanElement addIcon;
    @UiField
    SpanElement add;
    @UiField
    SpanElement clearIcon;
    @UiField
    SpanElement clear;
    @UiField
    Button clearButton;

    @Inject
    Provider<SelectItemView> itemViewProvider;

    List< SelectItemView > itemViews = new ArrayList<SelectItemView >();

    private boolean isEnabled = true;

    interface SelectorUiBinder extends UiBinder< HTMLPanel, MultipleInputSelector > {}
    private static SelectorUiBinder ourUiBinder = GWT.create( SelectorUiBinder.class );
}