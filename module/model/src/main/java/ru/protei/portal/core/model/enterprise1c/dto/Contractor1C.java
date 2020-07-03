package ru.protei.portal.core.model.enterprise1c.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.enterprise1c.annotation.UrlName1C;

@JsonInclude(JsonInclude.Include.NON_NULL)
@UrlName1C("Catalog_Контрагенты")
public class Contractor1C {

    @JsonProperty("Ref_Key")
    private String refKey;

    @JsonProperty("Parent_Key")
    private String parentKey;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("НаименованиеПолное")
    private String fullName;

    @JsonProperty("СтранаРегистрации_Key")
    private String registrationCountryKey;

    @JsonProperty("ИНН")
    private String inn;

    @JsonProperty("КПП")
    private String kpp;

    @JsonProperty("ИННВведенКорректно")
    private Boolean correctInn;

    @JsonProperty("КППВведенКорректно")
    private Boolean correctKpp;

    @JsonProperty("РасширенноеПредставлениеИНН")
    private String extendedInn;

    @JsonProperty("РасширенноеПредставлениеКПП")
    private String extendedKpp;

    @JsonProperty("DeletionMark")
    private Boolean deletionMark;

    @JsonProperty("Комментарий")
    private String comment;

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRegistrationCountryKey() {
        return registrationCountryKey;
    }

    public void setRegistrationCountryKey(String registrationCountryKey) {
        this.registrationCountryKey = registrationCountryKey;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public Boolean getCorrectInn() {
        return correctInn;
    }

    public void setCorrectInn(Boolean correctInn) {
        this.correctInn = correctInn;
    }

    public Boolean getCorrectKpp() {
        return correctKpp;
    }

    public void setCorrectKpp(Boolean correctKpp) {
        this.correctKpp = correctKpp;
    }

    public String getExtendedInn() {
        return extendedInn;
    }

    public void setExtendedInn(String extendedInn) {
        this.extendedInn = extendedInn;
    }

    public String getExtendedKpp() {
        return extendedKpp;
    }

    public void setExtendedKpp(String extendedKpp) {
        this.extendedKpp = extendedKpp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getDeletionMark() {
        return deletionMark;
    }

    public void setDeletionMark(Boolean deletionMark) {
        this.deletionMark = deletionMark;
    }

    @Override
    public String toString() {
        return "Contractor1C{" +
                "refKey='" + refKey + '\'' +
                ", parentKey='" + parentKey + '\'' +
                ", description='" + description + '\'' +
                ", fullName='" + fullName + '\'' +
                ", registrationCountryKey='" + registrationCountryKey + '\'' +
                ", inn='" + inn + '\'' +
                ", kpp='" + kpp + '\'' +
                ", correctInn=" + correctInn +
                ", correctKpp=" + correctKpp +
                ", extendedInn='" + extendedInn + '\'' +
                ", extendedKpp='" + extendedKpp + '\'' +
                ", deletionMark='" + deletionMark + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
