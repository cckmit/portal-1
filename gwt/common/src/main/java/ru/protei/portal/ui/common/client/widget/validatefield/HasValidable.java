package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.user.client.ui.HasText;

/**
 * Интерфес установления/снятия ошибки
 */
public interface HasValidable extends HasText{

    void makeAsCorrect();
    void makeAsIncorrect();

}
