package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import ru.protei.portal.ui.common.client.selector.util.ValueChangeButton;

import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.selector.util.ValueChangeButton.getValueChangeButton;
import static ru.protei.portal.ui.common.client.selector.util.ValueChangeButton.isValueChangeButton;

public abstract class AbstractSelectorItem extends Composite implements HasValueChangeButtonHandlers {
    @Override
    public void addValueChangeButtonHandler(Consumer<ValueChangeButton> valueChangeButtonHandler) {
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
            if (!isValueChangeButton(event.getNativeKeyCode())) {
                return;
            }

            event.preventDefault();
            changeState();
            valueChangeButtonHandler.accept(getValueChangeButton(event.getNativeKeyCode()));
        }, KeyDownEvent.getType());
    }

    protected void changeState() {}

    private Consumer<ValueChangeButton> valueChangeButtonHandler = valueChangeButton -> {};
    private Runnable valueChangeMouseHandler = () -> {};
}
