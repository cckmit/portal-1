package ru.protei.portal.ui.common.client.widget.selector.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
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
        setButtonState(false);
    }

    @UiHandler("searchField")
    public void onChange(KeyUpEvent event){
        String searchText = searchField.getValue().trim().toLowerCase();
        T caughtOption = null; // если null то известное

        super.clearOptions();

        if(searchText.isEmpty()) {
            options.entrySet().stream().forEachOrdered(entry -> super.addOption(entry.getKey(), entry.getValue()));
        }else{
            for ( Map.Entry< String, T > entry : options.entrySet() ) {
                String entryVal = entry.getKey().toLowerCase();


                if(entryVal.contains(searchText)){
                    super.addOption(entry.getKey(), entry.getValue());
                    if(entryVal.equals(searchText)){
                        caughtOption = entry.getValue();
                    }
                }
            }
        }

        ValueChangeEvent.fire(this, caughtOption);
        showPopup(root);
        setButtonState(true);
    }

    @UiHandler("button")
    public void onButtonClick(ClickEvent event){
        showPopup(root);
        setButtonState(true);
    }


    public void clearOptions(){
        options.clear();
        super.clearOptions();
    }

    public void addOption( String name, T value ) {
        if(options.containsValue(value)){
            // удаляем дубликаты значений

            options.keySet().stream().forEach(key -> {
                if(options.get(key).equals(value))
                    options.remove(key);
            });

            options.put(name, value);
        }

        options.put(name, value);
        super.addOption(name, value);
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

    native public static void console(String msg) /*-{
        console.log("me: " + msg);
    }-*/;

    native public static void debugger() /*-{
        debugger;
    }-*/;


    @UiField
    HTMLPanel root;
    @UiField
    LabelElement label;
    @UiField
    TextBox searchField;
    @UiField
    Button button;

    private Map<String, T> options = new HashMap<>();

    private static inputSelectorUiBinder ourUiBinder = GWT.create(inputSelectorUiBinder.class);
    interface inputSelectorUiBinder extends UiBinder<HTMLPanel, InputSelector> {}
}