package ru.protei.portal.ui.common.client.widget.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.model.marker.HasProcessable;

@SuppressWarnings("deprecation")
public class ButtonProcessable extends Composite implements HasProcessable,
        HasText, HasHTML, HasSafeHtml,
        SourcesClickEvents,
        HasClickHandlers, HasDoubleClickHandlers, HasFocus, HasEnabled,
        HasAllDragAndDropHandlers, HasAllFocusHandlers, HasAllGestureHandlers,
        HasAllKeyHandlers, HasAllMouseHandlers, HasAllTouchHandlers,
        SourcesMouseEvents {

    public ButtonProcessable() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public boolean isProcessing() {
        return stateStillContainer.hasClassName("hide");
    }

    @Override
    public void setProcessing(boolean isProcessing) {
        stateProcessingContainer.removeClassName("hide");
        stateStillContainer.removeClassName("hide");
        if (isProcessing) {
            stateStillContainer.addClassName("hide");
        } else {
            stateProcessingContainer.addClassName("hide");
        }
        button.setEnabled(!isProcessing);
    }


    @Override
    public void setHTML(SafeHtml html) {
        setHTML(html.asString());
    }

    @Override
    public void setHTML(String html) {
        stateStillContainer.setInnerHTML(html);
    }

    @Override
    public void setText(String text) {
        stateStillContainer.setInnerText(text);
    }

    @Override
    public String getHTML() {
        return stateStillContainer.getInnerHTML();
    }

    @Override
    public String getText() {
        return stateStillContainer.getInnerText();
    }


    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return button.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return button.addClickHandler(handler);
    }

    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return button.addDoubleClickHandler(handler);
    }

    @Override
    public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
        return button.addDragEndHandler(handler);
    }

    @Override
    public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
        return button.addDragEnterHandler(handler);
    }

    @Override
    public HandlerRegistration addDragHandler(DragHandler handler) {
        return button.addDragHandler(handler);
    }

    @Override
    public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
        return button.addDragLeaveHandler(handler);
    }

    @Override
    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return button.addDragOverHandler(handler);
    }

    @Override
    public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
        return button.addDragStartHandler(handler);
    }

    @Override
    public HandlerRegistration addDropHandler(DropHandler handler) {
        return button.addDropHandler(handler);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return button.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addGestureChangeHandler(GestureChangeHandler handler) {
        return button.addGestureChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addGestureEndHandler(GestureEndHandler handler) {
        return button.addGestureEndHandler(handler);
    }

    @Override
    public HandlerRegistration addGestureStartHandler(GestureStartHandler handler) {
        return button.addGestureStartHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return button.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return button.addKeyPressHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return button.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return button.addMouseDownHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return button.addMouseMoveHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return button.addMouseOutHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return button.addMouseOverHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return button.addMouseUpHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return button.addMouseWheelHandler(handler);
    }

    @Override
    public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
        return button.addTouchCancelHandler(handler);
    }

    @Override
    public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
        return button.addTouchEndHandler(handler);
    }

    @Override
    public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
        return button.addTouchMoveHandler(handler);
    }

    @Override
    public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
        return button.addTouchStartHandler(handler);
    }

    @Override
    public int getTabIndex() {
        return button.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        button.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        button.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        button.setTabIndex(index);
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }

    @Override
    public void addClickListener(ClickListener listener) {
        button.addClickListener(listener);
    }

    @Override
    public void removeClickListener(ClickListener listener) {
        button.removeClickListener(listener);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        button.addFocusListener(listener);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        button.removeFocusListener(listener);
    }

    @Override
    public void addKeyboardListener(KeyboardListener listener) {
        button.addKeyboardListener(listener);
    }

    @Override
    public void removeKeyboardListener(KeyboardListener listener) {
        button.removeKeyboardListener(listener);
    }

    @Override
    public void addMouseListener(MouseListener listener) {
        button.addMouseListener(listener);
    }

    @Override
    public void removeMouseListener(MouseListener listener) {
        button.removeMouseListener(listener);
    }

    @UiField
    Button button;
    @UiField
    DivElement stateProcessingContainer;
    @UiField
    DivElement stateStillContainer;

    interface ButtonProcessableUiBinder extends UiBinder<Button, ButtonProcessable> {}
    private static ButtonProcessableUiBinder ourUiBinder = GWT.create(ButtonProcessableUiBinder.class);
}
