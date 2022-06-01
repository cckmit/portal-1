package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@ApiModel(value="Delivery Specification Create Request")
public class DeliverySpecificationCreateRequest {
    @NotNull
    @JsonProperty("specifications")
    private List<DeliverySpecification> specifications;

    @NotNull
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
