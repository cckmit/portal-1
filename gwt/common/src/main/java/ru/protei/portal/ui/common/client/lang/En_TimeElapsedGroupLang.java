package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedGroup;

public class En_TimeElapsedGroupLang {
    @Inject
    public En_TimeElapsedGroupLang(Lang lang) {
        this.lang = lang;
    }

    public String getName(En_TimeElapsedGroup value) {
        if (value == null) {
            return lang.errUnknownResult();
        }

        switch (value) {
            case TYPE:
                return lang.timeElapsedGroupType();
            case DEPARTMENT:
                return lang.timeElapsedGroupDepartment();
            case AUTHOR:
                return lang.timeElapsedGroupAuthor();
            default: return lang.unknownField();
        }
    }

    private Lang lang;
}
