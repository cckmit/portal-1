package ru.protei.portal.core.model.yt.fields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by admin on 15/11/2017.
 */
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "name", visible = true, defaultImpl = Void.class)
@JsonSubTypes( {
        @JsonSubTypes.Type( name = "created", value = DateField.class ),
        @JsonSubTypes.Type( name = "updated", value = DateField.class ),
        @JsonSubTypes.Type( name = "resolved", value = DateField.class ),
        @JsonSubTypes.Type( name = "numberInProject", value = NumberField.class ),
        @JsonSubTypes.Type( name = "commentsCount", value = NumberField.class ),
        @JsonSubTypes.Type( name = "votes", value = NumberField.class ),
        @JsonSubTypes.Type( name = "links", value = LinkArrayField.class ),
        @JsonSubTypes.Type( name = "Priority", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "Type", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "State", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "Спринт", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "sprint", value = IdValueArrayField.class ),
        @JsonSubTypes.Type( name = "SP", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "projectShortName", value = StringField.class ),
        @JsonSubTypes.Type( name = "summary", value = StringField.class ),
        @JsonSubTypes.Type( name = "description", value = StringField.class ),
        @JsonSubTypes.Type( name = "updaterName", value = StringField.class ),
        @JsonSubTypes.Type( name = "updaterFullName", value = StringField.class ),
        @JsonSubTypes.Type( name = "reporterName", value = StringField.class ),
        @JsonSubTypes.Type( name = "reporterFullName", value = StringField.class ),
        @JsonSubTypes.Type( name = "Assignee", value = PersonArrayField.class ),
        @JsonSubTypes.Type( name = "Reviewer", value = PersonArrayField.class ),
        @JsonSubTypes.Type( name = "Заказчик", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "Subsystem", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "Номер обращения в CRM", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "attachments", value = AttachmentArrayField.class ),
        @JsonSubTypes.Type( name = "Оценка", value = StringArrayField.class ),
        @JsonSubTypes.Type( name = "Компонент", value = StringArrayField.class ),

        @JsonSubTypes.Type( name = "Затраченное время", value = StringField.class ),
        @JsonSubTypes.Type( name = "Подсистема", value = StringField.class ),
        @JsonSubTypes.Type( name = "Исполнитель", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Версии исправления", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Версия исправления", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Состояние", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Логи", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Приоритет", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Статус", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Тип", value = StringField.class ),
//        @JsonSubTypes.Type( name = "tags", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Качество задачи", value = StringField.class ),
//        @JsonSubTypes.Type( name = "project", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Рецензент", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Версия", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Дедлайн", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Обнаружено в версии", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Статус заказа", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Этап_old", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Этап", value = StringField.class ),
//        @JsonSubTypes.Type( name = "BOM", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Монтажка", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Программа", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Трафарет", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Регрессия", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Exp", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Версия релиза", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Stage", value = StringField.class ),
//        @JsonSubTypes.Type( name = "Версия системы", value = StringField.class ),
} )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
    public String name;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                '}';
    }
}
