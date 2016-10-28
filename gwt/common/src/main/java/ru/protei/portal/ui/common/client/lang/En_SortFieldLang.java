package ru.protei.portal.ui.common.client.lang;

import ru.protei.portal.core.model.dict.En_SortField;

import javax.inject.Inject;

/**
 * Поля сортировки
 */
public class En_SortFieldLang {
    public String getName( En_SortField value ) {
        switch (value)
        {
            case creation_date:
                return lang.created();
            case prod_name:
                return lang.name();
            case comp_name:
                return lang.name();
            case last_update:
                return lang.updated();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
