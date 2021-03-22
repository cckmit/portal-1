package ru.protei.portal.ui.common.client.selector;

public interface HasValueChangeButtonHandlers {
    void addValueChangeButtonHandler(Runnable valueChangeButtonHandler);
    void addValueChangeMouseClickHandler(Runnable valueChangeMouseHandler);
}
