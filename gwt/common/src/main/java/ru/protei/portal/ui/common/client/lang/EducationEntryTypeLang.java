package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.EducationEntryType;

public class EducationEntryTypeLang {

    public String getName(EducationEntryType type) {
        if (type == null) {
            return "?";
        }
        switch (type) {
            case CONFERENCE: return lang.educationConference();
            case COURSE: return lang.educationCourse();
            case LITERATURE: return lang.educationLiterature();
        }
        return "?";
    }

    @Inject
    private Lang lang;
}
