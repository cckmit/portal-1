package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;

public class TimeElapsedTypeLang {

    public String getName( En_TimeElapsedType elapsedType ) {
        if (elapsedType == null)
            return lang.unknownField();
        switch (elapsedType) {
            case NONE:
                return lang.timeElapsedTypeNone();
            case WATCH:
                return lang.timeElapsedTypeWatch();
            case NIGHT_WORK:
                return lang.timeElapsedTypeNightWork();
            case SOFT_INSTALL:
                return lang.timeElapsedTypeSoftInstall();
            case SOFT_UPDATE:
                return lang.timeElapsedTypeSoftUpdate();
            case SOFT_CONFIG:
                return lang.timeElapsedTypeSoftConfig();
            case TESTING:
                return lang.timeElapsedTypeTesting();
            case CONSULTATION:
                return lang.timeElapsedTypeConsultation();
            case MEETING:
                return lang.timeElapsedTypeMeeting();
            case DISCUSSION_OF_IMPROVEMENTS:
                return lang.timeElapsedTypeDiscussionOfImprovements();
            case LOG_ANALYSIS:
                return lang.timeElapsedTypeLogAnalysis();
            case SOLVE_PROBLEMS:
                return lang.timeElapsedTypeSolveProblems();

        }
        return lang.unknownField();
    }

    @Inject
    Lang lang;
}
