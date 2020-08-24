package ru.protei.portal.ui.absence.client.widget.datetime;

/**
 * Для сущностей, способных увеличиваться/уменьшаться
 */
public interface HasVaryAbility {
    boolean isVaryAble();
    void setVaryAble(boolean isVaryAble);
}
