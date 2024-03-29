package ru.protei.portal.ui.common.client.widget.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

/**
 * Вид селектора
 *  @deprecated  следует использовать {@link FormPopupSingleSelector}
 */
@Deprecated
public class FormSelector<T> extends Selector<T> implements HasValidable, HasEnabled {
    public FormSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Inject
    public void onInit() {
        initHandler();
        root.add(popup);
    }

    @Override
    public void fillSelectorView(DisplayOption selectedValue) {
        if ( selectedValue == null ) {
            return;
        }

        String valueName = selectedValue.getName() == null ? "" : selectedValue.getName();

        String innerHtml = "";
        if ( selectedValue.getIcon() != null ) {
            innerHtml += "<i class='" + selectedValue.getIcon() + "'></i>";
        }
        innerHtml += valueName;

        String title = isNotEmpty(selectedValue.getTitle()) ? selectedValue.getTitle() : valueName;

        text.setInnerHTML(innerHtml);
        if (isNotEmpty(title)) {
            text.setTitle(title);
        }

        if (selectedValue.getExternalLink() != null) {
            Element element = DOM.createAnchor();
            element.addClassName("fa fa-share m-l-5");
            element.setAttribute("href", selectedValue.getExternalLink());
            element.setAttribute("target", "_blank");
            addOnAnchorClickListener(element, popup);
            text.appendChild(element);
        }
    }



    @Override
    public void onSelectorItemSelect(SelectorItem item) {
        formContainer.removeStyleName(FOCUS_STYLENAME);
        super.onSelectorItemSelect(item);

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
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid){
        if(isValid)
            formContainer.removeStyleName(ERROR_STYLENAME);
        else
            formContainer.addStyleName(ERROR_STYLENAME);
    }

    @Override
    public boolean isEnabled() {
        return formContainer.getStyleName().contains(DISABLE_STYLENAME);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if ( enabled ) {
            formContainer.removeStyleName(DISABLE_STYLENAME);
            return;
        }
        formContainer.addStyleName(DISABLE_STYLENAME);
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    public void setHeader( String header ) {
        label.removeClassName("hide");
        label.setInnerText( header );
    }

    public void setMandatory( boolean mandatory ) {
        if ( mandatory ) {
            formContainer.addStyleName(REQUIRED_STYLENAME);
            return;
        }
        formContainer.removeStyleName(REQUIRED_STYLENAME);
    }

    public void setEnsureDebugId(String debugId) {
        formContainer.ensureDebugId(debugId);
        text.setId(DebugIds.DEBUG_ID_PREFIX + debugId + "-text");
    }

    public void ensureLabelDebugId(String debugId) {
        label.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    private void initHandler() {
        formContainer.sinkEvents(Event.ONCLICK);
        formContainer.addHandler(event -> {
            formContainer.addStyleName(FOCUS_STYLENAME);
            if (!popup.isVisible()) {
                showPopup(formContainer);
            }
        }, ClickEvent.getType());

        popup.addCloseHandler(event -> formContainer.removeStyleName(FOCUS_STYLENAME));
    }

    private native void addOnAnchorClickListener(Element element, SelectorPopup popup) /*-{
        element.addEventListener("click", function (event) {
            event.stopPropagation();
            popup.@ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable.ArrowSelectableSelectorPopup::hide()();
        })
    }-*/;

    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel formContainer;
    @UiField
    LabelElement label;
    @UiField
    SpanElement text;

    private boolean isValidable;

    private static final String ERROR_STYLENAME ="has-error";
    private static final String REQUIRED_STYLENAME ="required";
    private static final String DISABLE_STYLENAME ="disabled";
    private static final String FOCUS_STYLENAME ="focused";

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, FormSelector> { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}
