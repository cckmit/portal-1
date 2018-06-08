package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortDir;

/**
 * Способ сортировки
 */
public class En_SortDirLang {

    public String getName(En_SortDir value) {
        switch (value) {
            case ASC: return lang.asc();
            case DESC: return lang.desc();
            default: return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
