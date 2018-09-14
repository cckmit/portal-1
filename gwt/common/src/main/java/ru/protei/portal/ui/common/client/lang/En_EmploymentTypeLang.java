package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EmploymentType;

public class En_EmploymentTypeLang {

    public String getName(En_EmploymentType employmentType) {
        if (employmentType == null)
            return "";
        switch (employmentType) {
            case FULL_TIME:
                return lang.employmentTypeFullTime();
            case PART_TIME:
                return lang.employmentTypePartTime();
            case REMOTE:
                return lang.employmentTypeRemote();
            case CONTRACT:
                return lang.employmentTypeContract();
        }
        return lang.unknownField();
    }

    @Inject
    Lang lang;
}
