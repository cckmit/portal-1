package ru.protei.portal.core.model.enterprise1c.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.lang.En_1CParamType;
import ru.protei.portal.core.model.enterprise1c.annotation.SpecialParam1C;
import ru.protei.portal.core.model.enterprise1c.annotation.UrlName1C;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@UrlName1C("Catalog_ДоговорыКонтрагентов")
public class Contract1C {

    @SpecialParam1C(En_1CParamType.ID)
    @JsonProperty("Ref_Key")
    private String refKey;

    @JsonProperty("Номер")
    private String number;

    @JsonProperty("Description")
    private String name;

    @SpecialParam1C(En_1CParamType.ID)
    @JsonProperty("Owner_Key")
    private String contractorKey;

    @JsonProperty("Дата")
    private String dateSigning;

    @JsonProperty("DeletionMark")
    private Boolean deletionMark;

    @JsonProperty("ДополнительныеРеквизиты")
    private List<ContractAdditionalProperty1C> additionalProperties;

    @JsonProperty("ВидВзаиморасчетов")
    private String calculationType;

    public String getRefKey() { return refKey; }

    public void setRefKey(String refKey) { this.refKey = refKey; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getNumber() { return number; }

    public void setNumber(String number) { this.number = number; }

    public String getDateSigning() { return dateSigning;}

    public void setDateSigning(String dateSigning) { this.dateSigning = dateSigning; }

    public String getContractorKey() { return contractorKey; }

    public void setContractorKey(String contractorKey) { this.contractorKey = contractorKey; }

    public Boolean getDeletionMark() { return deletionMark; }

    public void setDeletionMark(Boolean deletionMark) { this.deletionMark = deletionMark; }

    public List<ContractAdditionalProperty1C> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<ContractAdditionalProperty1C> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public String getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }

    @Override
    public String toString() {
        return "Contract1C{" +
                "refKey='" + refKey + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", contractorKey='" + contractorKey + '\'' +
                ", dateSigning='" + dateSigning + '\'' +
                ", deletionMark=" + deletionMark +
                ", additionalProperties=" + additionalProperties +
                ", calculationType='" + calculationType + '\'' +
                '}';
    }
}