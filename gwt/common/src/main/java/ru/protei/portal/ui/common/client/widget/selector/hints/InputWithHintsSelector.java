package ru.protei.portal.ui.common.client.widget.selector.hints;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Текстовое поле с всплывающими подсказками
 */

public class InputWithHintsSelector<T> extends Selector<T> implements HasValidable, HasEnabled, HasText {
    public InputWithHintsSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setAutoSelectFirst(false);
    }

    @Override
    public void fillSelectorView(DisplayOption selectedValue) {
        text.setText(selectedValue == null ? "" : selectedValue.getName() == null ? "" : selectedValue.getName());
    }

    @Override
    public void onClick( ClickEvent event ) {
        super.onClick(event);
        if(isValidable)
            setValid( isValid() );
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        if(isValidable)
            setValid( isValid() );
    }

    @Override
    public String getText() {
        return text.getText();
    }

    @Override
    public void setText(String s) {
        text.setText(s);
    }

    @Override
    public boolean isValid(){
        return isValid;
    }

    @Override
    public void setValid(boolean isValid){
        this.isValid = isValid;
        if(isValid)
            text.removeStyleName(REQUIRED_STYLE_NAME);
        else
            text.addStyleName(REQUIRED_STYLE_NAME);
    }

    @Override
    public boolean isEnabled() {
        return text.isEnabled();
    }

    @Override
    public void setEnabled(boolean b) {
        text.setEnabled(b);
    }

    @UiHandler("text")
    public void onTextChange(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
            selectFirstElement();
            return;
        }
        textChangeTimer.cancel();
        textChangeTimer.schedule(300);
    }

    @UiHandler("text")
    public void onTextFocus(FocusEvent event) {
        textChangeTimer.cancel();
        textChangeTimer.schedule(300);
    }

    public void setHeader( String header ) {
        this.label.removeClassName( "hide" );
        this.label.setInnerText( header );
    }

    public void setHints(List<T> hints) {
        if (displayOptionCreator == null)
            return;

        if (hints == null) {
            hints = new LinkedList<>();
        }

        hintToOption.clear();

        for (T hint : hints) {
            DisplayOption option = displayOptionCreator.makeDisplayOption(hint);
            hintToOption.put(hint, option);
        }

        setFilteredHints();
    }

    public void setValidable(boolean validable) {
        isValidable = validable;
    }

    public void setPlaceholder(String placeholder) {
        text.getElement().setAttribute("placeholder", placeholder);
    }

    private void showHintsIfNotEmpty() {
        if (filteredHints.isEmpty())
            closePopup();
        else if (!popup.isAttached())
            showPopup();
    }

    private void setFilteredHints() {
        filteredHints = filterHintsByQuery(text.getText());
        clearOptions();
        filteredHints.forEach(this::addOption);
    }

    private List<T> filterHintsByQuery(String query) {
        return hintToOption.entrySet()
                .stream()
                .filter(e -> e.getValue().getName().contains(query))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void showPopup() {
        showPopup(text);
    }

    @UiField
    LabelElement label;
    @UiField
    TextBox text;

    protected Map<T, DisplayOption> hintToOption = new HashMap<>();

    private List<T> filteredHints = filterHintsByQuery("");
    private boolean isValidable = false;
    private boolean isValid;
    private static final String REQUIRED_STYLE_NAME ="required";

    private final Timer textChangeTimer = new Timer() {
        @Override
        public void run() {
            setFilteredHints();
            showHintsIfNotEmpty();
        }
    };

    interface InputWithHintsSelectorUiBinder extends UiBinder<HTMLPanel, InputWithHintsSelector> { }
    private static InputWithHintsSelectorUiBinder ourUiBinder = GWT.create(InputWithHintsSelectorUiBinder.class);

}