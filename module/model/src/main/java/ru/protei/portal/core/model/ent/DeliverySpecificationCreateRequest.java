package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class DeliverySpecificationCreateRequest {
    @JsonProperty("specifications")
    private List<DeliverySpecification> specifications;

    @JsonProperty("details")
    private List<DeliveryDetail> details;

    public DeliverySpecificationCreateRequest() {}

    public List<DeliverySpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<DeliverySpecification> specifications) {
        this.specifications = specifications;
    }

    public List<DeliveryDetail> getDetails() {
        return details;
    }

    public void setDetails(List<DeliveryDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "DeliverySpecificationCreateRequest{" +
                "specifications=" + specifications +
                ", details=" + details +
                '}';
    }
}
