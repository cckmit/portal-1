package ru.protei.portal.core.model.youtrack.dto.issue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.annotation.YtFieldName;
import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-Issue.html
 */
@YtEntityName("Issue")
public class YtIssue extends YtDto {

    @YtAlwaysInclude
    @YtFieldName("idReadable")
    public String idReadable;
    public String summary;
    public String description;
    public YtUser reporter;
    public YtUser updater;
    public YtProject project;
    public List<YtIssueCustomField> customFields;
    public List<YtIssueComment> comments;
    public List<YtIssueAttachment> attachments;

    @JsonIgnore
    public YtIssueCustomField getCrmNumberField() {
        return getField(CustomFieldNames.crmNumbers);
    }

    @JsonIgnore
    public YtIssueCustomField getProjectNumberField() {
        return getField(CustomFieldNames.projectNumbers);
    }

    @JsonIgnore
    public YtIssueCustomField getCustomerField() {
        return getField(CustomFieldNames.cumstomer);
    }

    @JsonIgnore
    public YtIssueCustomField getPriorityField() {
        return getField(CustomFieldNames.priority);
    }

    @JsonIgnore
    public YtIssueCustomField getStateField() {
        return getField(getStateFieldNames());
    }

    @JsonIgnore
    public YtIssueCustomField getField(String...fieldName) {
        List<String> fieldNames = Arrays.asList(fieldName);
        return CollectionUtils.stream(customFields)
                .filter(Objects::nonNull)
                .filter((field) -> fieldNames.contains(field.name))
                .findFirst()
                .orElse(null);
    }

    @JsonIgnore
    public static String[] getStateFieldNames() {
        return new String[] {
                CustomFieldNames.stateEng,
                CustomFieldNames.stateRus,
                CustomFieldNames.stateEquipmentRus,
                CustomFieldNames.stateAcrmRus
        };
    }

    public interface CustomFieldNames {
        String crmNumber = "Номер обращения в CRM";
        String crmNumbers = "Обращения в CRM";
        String projectNumbers = "Проекты CRM";
        String priority = "Priority";
        String stateEng = "State";
        String stateRus = "Состояние";
        String stateEquipmentRus = "Статус заказа";
        String stateAcrmRus = "Статус заявки";
        String requestType = "Тип заявки";
        String cumstomer = "Заказчик";
    }

    @Override
    public String toString() {
        return "YtIssue{" +
                "idReadable='" + idReadable + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", reporter=" + reporter +
                ", updater=" + updater +
                ", project=" + project +
                ", customFields=" + customFields +
                ", comments=" + comments +
                ", attachments=" + attachments +
                '}';
    }
}
