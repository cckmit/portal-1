package ru.protei.portal.core.model.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_Currency;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public class ApiContract implements Serializable {

    @JsonProperty("ref_key")
    private String refKey;

    @JsonProperty("date_signing")
    private Date dateSigning;

    @JsonProperty("cost")
    private Double cost;

    @JsonProperty("cost_currency")
    private En_Currency currency;

    @JsonProperty("cost_vat")
    private Long vat;

    @JsonProperty("subject")
    private String description;

    @JsonProperty("directions")
    private String directions;

    @JsonProperty("is_ministry_of_defence")
    private Boolean isMinistryOfDefence;

    @JsonProperty("dates")
    private List<ApiContractDate> dates;

    @JsonProperty("calculation_type")
    private String calculationType;

    public ApiContract() {
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public Date getDateSigning() {
        return dateSigning;
    }

    public void setDateSigning(Date dateSigning) {
        this.dateSigning = dateSigning;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public En_Currency getCurrency() {
        return currency;
    }

    public void setCurrency(En_Currency currency) {
        this.currency = currency;
    }

    public Long getVat() {
        return vat;
    }

    public void setVat(Long vat) {
        this.vat = vat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public Boolean getMinistryOfDefence() {
        return isMinistryOfDefence;
    }

    public void setMinistryOfDefence(Boolean ministryOfDefence) {
        isMinistryOfDefence = ministryOfDefence;
    }

    public List<ApiContractDate> getDates() {
        return dates;
    }

    public void setDates(List<ApiContractDate> dates) {
        this.dates = dates;
    }

    public String getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }

    @Override
    public String toString() {
        return "ApiContract{" +
                "refKey='" + refKey + '\'' +
                ", dateSigning=" + dateSigning +
                ", cost=" + cost +
                ", currency=" + currency +
                ", vat=" + vat +
                ", description='" + description + '\'' +
                ", directions='" + directions + '\'' +
                ", isMinistryOfDefence=" + isMinistryOfDefence +
                ", dates=" + dates +
                ", calculationType='" + calculationType + '\'' +
                '}';
    }
}
