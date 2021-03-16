package ru.protei.portal.ui.common.client.selector;

import ru.protei.portal.ui.common.client.selector.util.ValueChangeButton;

import java.util.function.Consumer;

public interface HasValueChangeButtonHandlers {
    void addValueChangeButtonHandler(Consumer<ValueChangeButton> valueChangeButtonHandler);
    void addValueChangeMouseClickHandler(Runnable valueChangeMouseHandler);
}
