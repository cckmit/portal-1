package ru.protei.portal.core.model.enterprise1c.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.lang.En_1CParamType;
import ru.protei.portal.core.model.enterprise1c.annotation.SpecialParam1C;
import ru.protei.portal.core.model.enterprise1c.annotation.UrlName1C;

import java.text.SimpleDateFormat;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@UrlName1C("Catalog_ДоговорыКонтрагентов")
public class Contract1C {

    //"97d8882b-bb3a-11e8-80cb-ac1f6b010113"
    @SpecialParam1C(En_1CParamType.ID)
    @JsonProperty("Ref_Key")
    private String refKey;

    //"Зак.-спец.№29 от 06.08.18г. к Дог.№217091/0501 "
    @JsonProperty("Номер")
    private String number;

    //"Зак.-спец.№29 от 06.08.18г. к Дог.№217091/0501  РТК Оренбург"
    @JsonProperty("Description")
    private String name;

    //"a191c4b8-67c7-11de-a54e-001f3c01f807"
    @SpecialParam1C(En_1CParamType.ID)
    @JsonProperty("Owner_Key")
    private String contractorKey;

    //"Дата": "2018-08-06T00:00:00",
    @SpecialParam1C(En_1CParamType.DATE)
    @JsonProperty("Дата")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Date dateSigning;

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateSigning() {
        return dateSigning;
    }

    public void setDateSigning(Date dateSigning) { this.dateSigning = dateSigning; }

    public String getContractorKey() { return contractorKey; }

    public void setContractorKey(String contractorKey) { this.contractorKey = contractorKey; }

    @Override
    public String toString() {
        return "Contract1C{" +
                "refKey='" + refKey + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", contractorKey='" + contractorKey + '\'' +
                ", dateSigning=" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(dateSigning) +
                '}';
    }
}
