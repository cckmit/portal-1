package ru.protei.portal.core.model.youtrack.dto.customfield.project;

import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.customfield.YtCustomField;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-ProjectCustomField.html
 */
public class YtProjectCustomField extends YtDto {

    public YtCustomField field;
    public YtProject project;
    public Boolean canBeEmpty;
    public String emptyFieldText;

    @Override
    public String toString() {
        return "YtProjectCustomField{" +
                "field=" + field +
                ", project=" + project +
                ", canBeEmpty=" + canBeEmpty +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                '}';
    }
}
