package ru.protei.portal.core.model.helper;

public interface Transformer<I, O> {

    /**
     * Преобразовать один объект в другой
     */
    O transform(I input);
}
