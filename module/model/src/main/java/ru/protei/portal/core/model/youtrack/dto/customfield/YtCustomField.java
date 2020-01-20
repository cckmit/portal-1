package ru.protei.portal.core.model.youtrack.dto.customfield;

import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.customfield.project.YtProjectCustomField;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-CustomField.html
 */
public class YtCustomField extends YtDto {

    public String name;
    public String localizedName;
    public List<YtProjectCustomField> instances;

    @Override
    public String toString() {
        return "YtCustomField{" +
                "name='" + name + '\'' +
                ", localizedName='" + localizedName + '\'' +
                ", instances=" + instances +
                '}';
    }
}
