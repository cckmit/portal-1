package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;

import static ru.protei.portal.ui.common.client.selector.util.SelectorItemKeyboardKey.isSelectorItemKeyboardKey;

public abstract class AbstractSelectorItem extends Composite implements HasValueChangeButtonHandlers {
    @Override
    public void addValueChangeButtonHandler(Runnable valueChangeButtonHandler) {
        this.valueChangeButtonHandler = valueChangeButtonHandler;
    }

    @Override
    public void addValueChangeMouseClickHandler(Runnable valueChangeMouseHandler) {
        this.valueChangeMouseHandler = valueChangeMouseHandler;
    }

    protected void initHandlers() {
        addDomHandler(event -> {
            event.preventDefault();
            changeState();
            valueChangeMouseHandler.run();
        }, ClickEvent.getType());

        addDomHandler(event -> {
            if (!isSelectorItemKeyboardKey(event.getNativeKeyCode())) {
                return;
            }

            event.preventDefault();
            changeState();
            valueChangeButtonHandler.run();
        }, KeyDownEvent.getType());
    }

    protected void changeState() {}

    private Runnable valueChangeButtonHandler = () -> {};
    private Runnable valueChangeMouseHandler = () -> {};
}
