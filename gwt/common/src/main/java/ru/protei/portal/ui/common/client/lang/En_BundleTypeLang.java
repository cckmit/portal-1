package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_BundleType;

public class En_BundleTypeLang {

    public String getName(En_BundleType type) {

        if(type == null)
            return lang.unknownField();

        switch (type) {
            case LINKED_WITH:
                return lang.linkedWith();
            case PARENT_FOR:
                return lang.parentFor();
            case SUBTASK:
                return lang.subtask();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
