package ru.protei.portal.ui.equipment.client.widget.selector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.popup.AbstractNavigationHandler;
import ru.protei.portal.ui.equipment.client.common.EquipmentUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Виджет связанных устройств
 */
public class EquipmentSelector
        extends ButtonSelector<Equipment >
        implements ModelSelector<Equipment>, AbstractNavigationHandler
    {

        @Inject
        public void init( EquipmentModel model, Lang lang ) {
            this.lang = lang;
            model.subscribe( this );
            setSearchEnabled( true );
            setHasNullValue( true );
            setSearchAutoFocus(true);
            nullItemOption = new DisplayOption( lang.equipmentPrimaryUseNotDefinied() );
            setHandler(this);
        }

        @Override
        public void fillOptions( List< Equipment > options ) {
            this.options = options;
            clearOptions();
            if (hasNullValue) {
                addOption(lang.equipmentPrimaryUseNotDefinied(), null);
            }

            for ( Equipment value : options ) {
                StringBuilder sb = new StringBuilder();
                sb.append( value.getName() );
                if ( value.getDecimalNumbers() != null ) {
                    sb
                            .append( " (" )
                            .append( value.getDecimalNumbers().stream().map( EquipmentUtils:: formatNumber ).collect( Collectors.joining(", ")) )
                            .append( ")" );
                }

                addOption( sb.toString(), value );
            }
        }

        @Override
        public void onArrowUp() {
            if (selectedIndex == 0) {
                return;
            }
            unSelectPrevious(selectedIndex);
            selectedIndex -= 1;
            selectElement(selectedIndex);

        }

        @Override
        public void onArrowDown() {
            if (selectedIndex == options.size() - 1) {
                return;
            }
            unSelectPrevious(selectedIndex);
            selectedIndex += 1;
            selectElement(selectedIndex);
        }

        @Override
        public void onEnterClicked() {
            DisplayOption displayOption = itemToDisplayOptionModel.get(options.get(selectedIndex));
            fillSelectorView(displayOption);
            unSelectPrevious(selectedIndex);
            closePopup();
            ValueChangeEvent.fire(this, options.get(selectedIndex));
        }

        @Override
        public void selectFirst() {
            this.selectedIndex = 0;
            selectElement(0);
        }

        public void setHasNullValue(boolean hasNullValue) {
            this.hasNullValue = hasNullValue;
        }

        @Override
        public void refreshValue() {}

        private void unSelectPrevious(int selectedIndex) {
            itemToViewModel.get(options.get(selectedIndex)).unselect();
        }

        private void selectElement(int selectedIndex) {
            itemToViewModel.get(options.get(selectedIndex)).select();
        }

        private boolean hasNullValue;

        private List<Equipment> options;
        private int selectedIndex;

        private Lang lang;
    }