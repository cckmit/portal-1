package ru.protei.portal.ui.common.client.widget.optionlist.base;

import java.util.List;

/**
 * Интерфейс списка с моделью
 */
public interface ModelList< T > {

    void fillOptions( List< T > items );
}
