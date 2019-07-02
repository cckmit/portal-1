package ru.protei.portal.redmine.utils;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineProcessingException;
import org.slf4j.Logger;

public class LoggerUtils {

    public static void logRedmineException(Logger logger, RedmineException e) {
        if (e instanceof RedmineProcessingException) {
            logger.error(String.join(", ", ((RedmineProcessingException) e).getErrors()), e);
        } else {
            logger.error(e.getMessage(), e);
        }
    }
}
