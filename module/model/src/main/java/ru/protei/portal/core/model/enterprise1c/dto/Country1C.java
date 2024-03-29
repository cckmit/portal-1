package ru.protei.portal.core.model.enterprise1c.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.lang.En_1CParamType;
import ru.protei.portal.core.model.enterprise1c.annotation.SpecialParam1C;
import ru.protei.portal.core.model.enterprise1c.annotation.UrlName1C;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@UrlName1C("Catalog_СтраныМира")
public class Country1C {

    @SpecialParam1C(En_1CParamType.ID)
    @JsonProperty("Ref_Key")
    private String refKey;

    @JsonProperty("Description")
    private String name;

    @JsonProperty("НаименованиеПолное")
    private String fullName;

    @JsonProperty("DeletionMark")
    private Boolean deletionMark;

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getDeletionMark() {
        return deletionMark;
    }

    public void setDeletionMark(Boolean deletionMark) {
        this.deletionMark = deletionMark;
    }

    @Override
    public String toString() {
        return "Country1C{" +
                "refKey='" + refKey + '\'' +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", deletionMark='" + deletionMark + '\'' +
                '}';
    }
}
