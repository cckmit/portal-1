package ru.protei.portal.core.model.yt.api.customfield.project;

import ru.protei.portal.core.model.yt.api.YtDto;
import ru.protei.portal.core.model.yt.api.project.YtProject;
import ru.protei.portal.core.model.yt.api.customfield.YtCustomField;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-ProjectCustomField.html */
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
