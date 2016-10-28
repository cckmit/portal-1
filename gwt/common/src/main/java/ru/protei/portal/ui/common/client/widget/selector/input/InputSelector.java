package ru.protei.portal.ui.common.client.widget.selector.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Инпут селектор
 */
public class InputSelector<T> extends Selector<T> {
    public InputSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Inject
    public void onInit() {
        addCloseHandler(event -> setButtonState(false));
    }

    @Override
    public void fillSelectorView(String selectedValue) {
        searchField.setValue(selectedValue == null ? "" : selectedValue);
        isOptionExistsLastTime = true;
        setButtonState(false);
    }

    @UiHandler("searchField")
    public void onChange(KeyUpEvent event){
        String searchText = searchField.getValue().trim().toLowerCase();
        if(prevInputText.equalsIgnoreCase(searchText) )
            return;
        prevInputText = searchText;

        fillOptions(searchText);

        tryFireEvent(getOptionIfExists(searchText));

        showPopup(root);
        setButtonState(true);
    }

    @UiHandler("button")
    public void onButtonClick(ClickEvent event) {
        showPopup(root);
        setButtonState(true);
    }

    private void tryFireEvent(T caughtOption){
        if(isOptionExistsLastTime == null){
            isOptionExistsLastTime = caughtOption == null; // first time
        }

        if(caughtOption != null && !isOptionExistsLastTime){
            ValueChangeEvent.fire(this, caughtOption);
            isOptionExistsLastTime = true;
        }else if(caughtOption == null && isOptionExistsLastTime){
            ValueChangeEvent.fire(this, null);
            isOptionExistsLastTime = false;
        }
    }

    private void fillOptions(String searchText){
        super.clearOptions();
        initialOptions.entrySet().stream().forEach(entry -> {
            String optionName = entry.getKey().toLowerCase();
            if((searchText.isEmpty() ||  optionName.contains(searchText)) && !optionName.isEmpty())
                super.addOption(entry.getKey(), entry.getValue());
        });
    }

    private T getOptionIfExists(String searchText){
        Optional<Map.Entry<String, T>> result = initialOptions
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().equals(searchText))
                .findFirst();

        if(result.isPresent())
            return result.get().getValue();
        return null;
    }

    public void addHiddenOption(String name, T value){
        initialOptions.put(name, value);
    }

    public void addOption( String name, T value ) {
        addHiddenOption(name, value);
        super.addOption(name, value);
    }

    public void clearOptions( ) {
        initialOptions.clear();
        super.clearOptions();
    }

    public void removeOption(String name){
        initialOptions.remove(name);
    }

    private void setButtonState(boolean isActive){
        if(isActive)
            button.addStyleName("active");
        else
            button.removeStyleName("active");
    }

    public void closePopup(){
        super.closePopup();
        setButtonState(false);
    }

    public HasText inputText(){
        return searchField;
    }

    @UiField
    HTMLPanel root;
    @UiField
    LabelElement label;
    @UiField
    TextBox searchField;
    @UiField
    Button button;

    protected Map<String, T> initialOptions = new HashMap< >();
    Boolean isOptionExistsLastTime = null;
    String prevInputText = "";

    private static inputSelectorUiBinder ourUiBinder = GWT.create(inputSelectorUiBinder.class);
    interface inputSelectorUiBinder extends UiBinder<HTMLPanel, InputSelector> {}
}