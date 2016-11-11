package ru.protei.portal.ui.common.client.widget.validatefield;

/**
 * Интерфес установки ошибки
 */
public interface HasValidable{

    void setValid(boolean isValid);

    boolean isValid();
}
